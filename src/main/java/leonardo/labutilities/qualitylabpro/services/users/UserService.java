package leonardo.labutilities.qualitylabpro.services.users;

import java.time.LocalDateTime;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
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
		String message = String.format("""
				Dear user,

				Use the following temporary password to recover your account: %s

				Best regards,
				Your Team""", recoveryEmailDTO.temporaryPassword());
		log.info("Sending recovery identifier to: {}", recoveryEmailDTO.email());
		this.emailService
				.sendPlainTextEmail(new EmailDTO(recoveryEmailDTO.email(), subject, message));
	}

	public void recoverPassword(String username, String email) {

		var user = this.userRepository.existsByUsernameAndEmail(username, email);

		if (!user) {
			throw new CustomGlobalErrorHandling.UserNotFoundException();
		}

		String temporaryPassword = this.passwordRecoveryTokenManager.generateTemporaryPassword();
		this.passwordRecoveryTokenManager.generateAndStoreToken(email, temporaryPassword);

		this.sendRecoveryEmail(new RecoveryEmailDTO(email, temporaryPassword));
	}

	public void changePassword(String email, String temporaryPassword, String newPassword) {
		if (!this.passwordRecoveryTokenManager.isRecoveryTokenValid(temporaryPassword, email)) {
			throw new CustomGlobalErrorHandling.RecoveryTokenInvalidException();
		}
		this.userRepository.setPasswordWhereByEmail(email,
				BCryptEncoderComponent.encrypt(newPassword));
	}

	private TokenJwtDTO authenticateAndGenerateToken(User credential, String password) {
		try {
			final var authToken =
					new UsernamePasswordAuthenticationToken(credential.getUsername(), password);
			final var auth = this.authenticationManager.authenticate(authToken);
			final var user = (User) auth.getPrincipal();

			if (!auth.isAuthenticated()) {
				this.emailService.notifyFailedUserLogin(user.getUsername(), user.getEmail(),
						LocalDateTime.now());
				throw new BadCredentialsException(
						"Authentication failed for user: " + credential.getUsername());
			}

			return this.tokenService.generateToken(user);

		} catch (BadCredentialsException e) {
			log.error("Authentication failed for user: {}", credential.getUsername(), e);
			throw e;
		}
	}

	public User signUp(String username, String email, String password) {

		if (this.userRepository.existsByUsernameOrEmail(email, email)) {
			throw new CustomGlobalErrorHandling.UserAlreadyExistException();
		}

		var user =
				new User(username, BCryptEncoderComponent.encrypt(password), email, UserRoles.USER);
		var savedUser = this.userRepository.save(user);

		try {
			this.emailService.notifyUserSignup(savedUser.getUsername(), savedUser.getEmail(),
					LocalDateTime.now());
		} catch (Exception e) {
			log.error("Failed to send signup notification for user: {}. Exception: ",
					savedUser.getEmail(), e);
		}

		return savedUser;
	}

	public TokenJwtDTO signIn(String identifier, String password) {
		try {
			final var credential =
					this.userRepository.findOneByUsernameOrEmail(identifier, identifier);

			if (credential == null) {
				throw new CustomGlobalErrorHandling.UserNotFoundException();
			}

			return this.authenticateAndGenerateToken(credential, password);

		} catch (Exception e) {
			log.error("Sign-in process failed for identifier: {}", identifier, e);
			throw new BadCredentialsException("Sign-in failed for identifier: " + identifier);
		}
	}

	public void updateUserPassword(String name, String email, String password, String newPassword) {
		var oldPass = this.userRepository.getReferenceByUsernameAndEmail(name, email);
		boolean oldPasswordMatches =
				BCryptEncoderComponent.decrypt(password, oldPass.getPassword());
		boolean newPasswordMatches =
				BCryptEncoderComponent.decrypt(newPassword, oldPass.getPassword());
		if (!oldPasswordMatches || newPasswordMatches) {
			log.error("PasswordNotMatches. {}, {}", name, email);
			throw new CustomGlobalErrorHandling.PasswordNotMatchesException();
		} else {
			this.userRepository.setPasswordWhereByUsername(oldPass.getUsername(),
					BCryptEncoderComponent.encrypt(newPassword));
		}
	}
}
