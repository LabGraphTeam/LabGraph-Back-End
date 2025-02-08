package leonardo.labutilities.qualitylabpro.services.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import leonardo.labutilities.qualitylabpro.entities.User;
import leonardo.labutilities.qualitylabpro.enums.UserRoles;
import leonardo.labutilities.qualitylabpro.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.components.BCryptEncoderComponent;
import leonardo.labutilities.qualitylabpro.utils.components.PasswordRecoveryTokenManager;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordRecoveryTokenManager passwordRecoveryTokenManager;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private UserService userService;

	@Test
	void testRecoverPassword_UserExists() {
		when(this.userRepository.existsByUsernameAndEmail(anyString(), anyString()))
				.thenReturn(true);
		when(this.passwordRecoveryTokenManager.generateTemporaryPassword())
				.thenReturn("tempPassword");

		this.userService.recoverPassword("identifier", "identifier@example.com");

		verify(this.passwordRecoveryTokenManager).generateAndStoreToken("identifier@example.com",
				"tempPassword");
		verify(this.emailService).sendPlainTextEmail(any());
	}

	@Test
	void testRecoverPassword_UserDoesNotExist() {
		when(this.userRepository.existsByUsernameAndEmail(anyString(), anyString()))
				.thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.UserNotFoundException.class,
				() -> this.userService.recoverPassword("identifier", "identifier@example.com"));
	}

	@Test
	void testChangePassword_ValidToken() {
		when(this.passwordRecoveryTokenManager.isRecoveryTokenValid(anyString(), anyString()))
				.thenReturn(true);
		this.userService.changePassword("identifier@example.com", "tempPassword", "newPassword");
		assertThat(this.passwordRecoveryTokenManager.isRecoveryTokenValid("tempPassword",
				"identifier@example.com")).isTrue();
	}

	@Test
	void testChangePassword_InvalidToken() {
		when(this.passwordRecoveryTokenManager.isRecoveryTokenValid(anyString(), anyString()))
				.thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.RecoveryTokenInvalidException.class,
				() -> this.userService.changePassword("identifier@example.com", "tempPassword",
						"newPassword"));
	}

	@Test
	void testSignUp_UserAlreadyExists() {
		when(this.userRepository.existsByUsernameOrEmail(anyString(), anyString()))
				.thenReturn(true);

		assertThrows(CustomGlobalErrorHandling.UserAlreadyExistException.class,
				() -> this.userService.signUp("identifier", "identifier@example.com", "password"));
	}

	@Test
	void testSignUp_NewUser() {
		when(this.userRepository.existsByUsernameOrEmail(anyString(), anyString()))
				.thenReturn(false);
		when(this.userRepository.save(any(User.class))).thenReturn(new User("identifier",
				"encryptedPassword", "identifier@example.com", UserRoles.USER));

		User user = this.userService.signUp("identifier", "password", "identifier@example.com");

		assertNotNull(user);
		assertEquals("identifier", user.getUsername());
		assertEquals("identifier@example.com", user.getEmail());
	}

	@Test
	void should_return_error_with_testUpdateUserPassword_PasswordMatches() {
		User user = new User("identifier", BCryptEncoderComponent.encrypt("newPassword"),
				"identifier@example.com", UserRoles.USER);

		when(this.userRepository.getReferenceByUsernameAndEmail(anyString(), anyString()))
				.thenReturn(user);

		assertThrows(CustomGlobalErrorHandling.PasswordNotMatchesException.class,
				() -> this.userService.updateUserPassword("identifier", "identifier@example.com",
						"oldPassword", "newPassword"));
		verify(this.userRepository, never()).setPasswordWhereByUsername(anyString(), anyString());
	}

	@Test
	void testUpdateUserPassword_PasswordDoesNotMatch() {
		User user = new User("identifier", BCryptEncoderComponent.encrypt("oldPassword"),
				"identifier@example.com", UserRoles.USER);
		when(this.userRepository.getReferenceByUsernameAndEmail(anyString(), anyString()))
				.thenReturn(user);

		assertThrows(CustomGlobalErrorHandling.PasswordNotMatchesException.class,
				() -> this.userService.updateUserPassword("identifier", "identifier@example.com",
						"wrongPassword", "newPassword"));
	}
}
