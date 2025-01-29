package leonardo.labutilities.qualitylabpro.services;

import leonardo.labutilities.qualitylabpro.entities.User;
import leonardo.labutilities.qualitylabpro.enums.UserRoles;
import leonardo.labutilities.qualitylabpro.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.services.users.UserService;
import leonardo.labutilities.qualitylabpro.utils.components.BCryptEncoderComponent;
import leonardo.labutilities.qualitylabpro.utils.components.PasswordRecoveryTokenManager;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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
		when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(true);
		when(passwordRecoveryTokenManager.generateTemporaryPassword()).thenReturn("tempPassword");

		userService.recoverPassword("identifier", "identifier@example.com");

		verify(passwordRecoveryTokenManager).generateAndStoreToken("identifier@example.com",
				"tempPassword");
		verify(emailService).sendPlainTextEmail(any());
	}

	@Test
	void testRecoverPassword_UserDoesNotExist() {
		when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> userService.recoverPassword("identifier", "identifier@example.com"));
	}

	@Test
	void testChangePassword_ValidToken() {
		User user = new User("identifier", BCryptEncoderComponent.encrypt("newPassword"),
				"identifier@example.com", UserRoles.USER);
		when(passwordRecoveryTokenManager.isRecoveryTokenValid(anyString(), anyString()))
				.thenReturn(true);
		userService.changePassword("identifier@example.com", "tempPassword", "newPassword");
		assertThat(passwordRecoveryTokenManager.isRecoveryTokenValid("tempPassword",
				"identifier@example.com")).isTrue();
	}

	@Test
	void testChangePassword_InvalidToken() {
		when(passwordRecoveryTokenManager.isRecoveryTokenValid(anyString(), anyString()))
				.thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class, () -> userService
				.changePassword("identifier@example.com", "tempPassword", "newPassword"));
	}

	@Test
	void testSignUp_UserAlreadyExists() {
		when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(true);

		assertThrows(CustomGlobalErrorHandling.UserAlreadyExistException.class,
				() -> userService.signUp("identifier", "identifier@example.com", "password"));
	}

	@Test
	void testSignUp_NewUser() {
		when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
		when(userRepository.save(any(User.class))).thenReturn(new User("identifier",
				"encryptedPassword", "identifier@example.com", UserRoles.USER));

		User user = userService.signUp("identifier", "password", "identifier@example.com");

		assertNotNull(user);
		assertEquals("identifier", user.getUsername());
		assertEquals("identifier@example.com", user.getEmail());
	}

	@Test
	void should_return_error_with_testUpdateUserPassword_PasswordMatches() {
		User user = new User("identifier", BCryptEncoderComponent.encrypt("newPassword"),
				"identifier@example.com", UserRoles.USER);

		when(userRepository.getReferenceByUsernameAndEmail(anyString(), anyString()))
				.thenReturn(user);

		assertThrows(CustomGlobalErrorHandling.PasswordNotMatchesException.class,
				() -> userService.updateUserPassword("identifier", "identifier@example.com",
						"oldPassword", "newPassword"));
		verify(userRepository, never()).setPasswordWhereByUsername(anyString(), anyString());
	}

	@Test
	void testUpdateUserPassword_PasswordDoesNotMatch() {
		User user = new User("identifier", BCryptEncoderComponent.encrypt("oldPassword"),
				"identifier@example.com", UserRoles.USER);
		when(userRepository.getReferenceByUsernameAndEmail(anyString(), anyString()))
				.thenReturn(user);

		assertThrows(CustomGlobalErrorHandling.PasswordNotMatchesException.class,
				() -> userService.updateUserPassword("identifier", "identifier@example.com",
						"wrongPassword", "newPassword"));
	}
}
