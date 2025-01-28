package leonardo.labutilities.qualitylabpro.services.users;

import leonardo.labutilities.qualitylabpro.dtos.authentication.TokenJwtDTO;
import leonardo.labutilities.qualitylabpro.dtos.email.EmailDTO;
import leonardo.labutilities.qualitylabpro.dtos.email.RecoveryEmailDTO;
import leonardo.labutilities.qualitylabpro.entities.User;
import leonardo.labutilities.qualitylabpro.enums.UserRoles;
import leonardo.labutilities.qualitylabpro.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.services.authentication.TokenService;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.components.BCryptEncoderComponent;
import leonardo.labutilities.qualitylabpro.utils.components.PasswordRecoveryTokenManager;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final PasswordRecoveryTokenManager passwordRecoveryTokenManager;
	private final EmailService emailService;
	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;

	private void sendRecoveryEmail(RecoveryEmailDTO recoveryEmailDTO) {
		String subject = "Password Recovery";
		String message = String.format(
				"Dear user,\n\nUse the following temporary password to recover your account: %s\n\nBest regards,"
						+ "\nYour Team",
				recoveryEmailDTO.temporaryPassword());
		log.info("Sending recovery identifier to: {}", recoveryEmailDTO.email());
		emailService.sendPlainTextEmail(new EmailDTO(recoveryEmailDTO.email(), subject, message));
	}

	public void recoverPassword(String username, String email) {

		var user = userRepository.existsByUsernameAndEmail(username, email);

		if (!user) {
			throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"User not or invalid arguments");
		}

		String temporaryPassword = passwordRecoveryTokenManager.generateTemporaryPassword();
		passwordRecoveryTokenManager.generateAndStoreToken(email, temporaryPassword);

		sendRecoveryEmail(new RecoveryEmailDTO(email, temporaryPassword));
	}

	public void changePassword(String email, String temporaryPassword, String newPassword) {
		if (!passwordRecoveryTokenManager.isRecoveryTokenValid(temporaryPassword, email)) {
			throw new CustomGlobalErrorHandling.ResourceNotFoundException("Invalid recovery token");
		}
		userRepository.setPasswordWhereByEmail(email, BCryptEncoderComponent.encrypt(newPassword));
	}

	public User signUp(String username, String email, String password) {

		var user =
				new User(username, BCryptEncoderComponent.encrypt(password), email, UserRoles.USER);

		if (userRepository.existsByEmail(email)) {
			throw new CustomGlobalErrorHandling.UserAlreadyExistException();
		}
		try {
			emailService.notifyUserSignup(user.getUsername(), user.getEmail(), LocalDateTime.now());
		} catch (Exception e) {
			log.error("Failed signup for user: {}. Exception: ", user.getEmail(), e);
		}

		return userRepository.save(user);
	}

	public TokenJwtDTO signIn(String identifier, String password) {

		final var credential = userRepository.findByUsernameOrEmail(identifier, identifier).getUsername();

		final var authToken = new UsernamePasswordAuthenticationToken(credential, password);
		final var auth = authenticationManager.authenticate(authToken);
		final var user = (User) auth.getPrincipal();
		if (!auth.isAuthenticated()) {
			try {
				emailService.notifyFailedUserLogin(user.getUsername(), user.getEmail(),
						LocalDateTime.now());
			} catch (Exception e) {
				log.error("Failed to send analytics notification identifier", e);
			}
		}
		return tokenService.generateToken(user);
	}

	public void updateUserPassword(String name, String email, String password, String newPassword) {
		var oldPass = userRepository.getReferenceByUsernameAndEmail(name, email);
		if (!BCryptEncoderComponent.decrypt(password, oldPass.getPassword())
				|| BCryptEncoderComponent.decrypt(newPassword, oldPass.getPassword())) {
			log.error("PasswordNotMatches. {}, {}", name, email);
			throw new CustomGlobalErrorHandling.PasswordNotMatchesException();
		} else {
			userRepository.setPasswordWhereByUsername(oldPass.getUsername(),
					BCryptEncoderComponent.encrypt(newPassword));
		}
	}
}
