package leonardo.labutilities.qualitylabpro.domains.users.services;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.dtos.TokenJwtDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.services.TokenService;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.utils.AuthenticatedUserProvider;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;
import leonardo.labutilities.qualitylabpro.domains.users.components.PasswordRecoveryTokenManager;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import leonardo.labutilities.qualitylabpro.domains.users.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.domains.users.utils.BCryptEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordRecoveryTokenManager passwordRecoveryTokenManager;

	@Mock
	private EmailService emailService;

	@Mock
	private TokenService tokenService;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private UserService userService;

	@Test
	void testRecoverPassword_UserExists() {
		when(this.userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(true);

		this.userService.recoverPassword("identifier", "identifier@example.com");

		verify(this.passwordRecoveryTokenManager).generateAndStoreToken("identifier@example.com");
		verify(this.emailService).sendPlainTextEmail(any());
	}

	@Test
	void testRecoverPassword_UserDoesNotExist() {
		when(this.userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.UserNotFoundException.class,
				() -> this.userService.recoverPassword("identifier", "identifier@example.com"));
	}

	@Test
	void testChangePassword_ValidToken() {
		when(this.passwordRecoveryTokenManager.isRecoveryTokenValid(anyString(), anyString())).thenReturn(true);
		this.userService.changePassword("identifier@example.com", "tempPassword", "newPassword");
		assertThat(this.passwordRecoveryTokenManager.isRecoveryTokenValid("tempPassword", "identifier@example.com"))
				.isTrue();
	}

	@Test
	void testChangePassword_InvalidToken() {
		when(this.passwordRecoveryTokenManager.isRecoveryTokenValid(anyString(), anyString())).thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.RecoveryTokenInvalidException.class,
				() -> this.userService.changePassword("identifier@example.com", "tempPassword", "newPassword"));
	}

	@Test
	void testSignUp_UserAlreadyExists() {
		when(this.userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(true);

		assertThrows(CustomGlobalErrorHandling.UserAlreadyExistException.class,
				() -> this.userService.signUp("identifier", "identifier@example.com", "password"));
	}

	@Test
	void testSignUp_NewUser() {
		when(this.userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(false);
		when(this.userRepository.save(any(User.class)))
				.thenReturn(new User("identifier", "encryptedPassword", "identifier@example.com"));

		User user = this.userService.signUp("identifier", "password", "identifier@example.com");

		assertNotNull(user);
		assertEquals("identifier", user.getUsername());
		assertEquals("identifier@example.com", user.getEmail());
	}

	@Test
	void should_return_error_with_testUpdateUserPassword_PasswordMatches() {
		User user = new User("identifier", BCryptEncoder.encrypt("newPassword"), "identifier@example.com");

		when(this.userRepository.getReferenceByUsernameAndEmail(anyString(), anyString())).thenReturn(user);

		assertThrows(CustomGlobalErrorHandling.PasswordNotMatchesException.class, () -> this.userService
				.updateUserPassword("identifier", "identifier@example.com", "oldPassword", "newPassword"));
		verify(this.userRepository, never()).setPasswordWhereByUsername(anyString(), anyString());
	}

	@Test
	void testUpdateUserPassword_PasswordDoesNotMatch() {
		User user = new User("identifier", BCryptEncoder.encrypt("oldPassword"), "identifier@example.com");
		when(this.userRepository.getReferenceByUsernameAndEmail(anyString(), anyString())).thenReturn(user);

		assertThrows(CustomGlobalErrorHandling.PasswordNotMatchesException.class, () -> this.userService
				.updateUserPassword("identifier", "identifier@example.com", "wrongPassword", "newPassword"));
	}

	@Test
	void testFindAnalyticsByUserValidated() {
		Pageable pageable = Pageable.unpaged();
		List<AnalyticsDTO> expectedList = createSampleRecordList();
		Page<AnalyticsDTO> expectedPage = new PageImpl<>(expectedList);
		User mockUser = new User();
		mockUser.setId(1L);

		try (MockedStatic<AuthenticatedUserProvider> mockedStatic =
				Mockito.mockStatic(AuthenticatedUserProvider.class)) {
			mockedStatic.when(AuthenticatedUserProvider::getCurrentAuthenticatedUser).thenReturn(mockUser);

			when(this.userRepository.findAnalyticsByUserValidatedId(eq(1L), any(Pageable.class)))
					.thenReturn(expectedPage);

			Page<AnalyticsDTO> result = this.userService.findAnalyticsByUserValidated(pageable);

			assertNotNull(result);
			assertEquals(expectedList.size(), result.getContent().size());

			verify(this.userRepository).findAnalyticsByUserValidatedId(1L, pageable);
		}
	}

	@Test
	void testSignIn_UserNotFound() {
		when(this.userRepository.findOneByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
		assertThrows(CustomGlobalErrorHandling.UserNotFoundException.class,
				() -> this.userService.signIn("username", "password"));
	}

	@Test
	void testSignIn_Successful() {
		User user = new User("username", "encrypted", "user@example.com");
		when(this.userRepository.findOneByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));

		Authentication auth = mock(Authentication.class);
		when(auth.isAuthenticated()).thenReturn(true);
		when(auth.getPrincipal()).thenReturn(user);
		when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

		TokenJwtDTO token = new TokenJwtDTO("tokenValue", null);
		when(this.tokenService.generateToken(user)).thenReturn(token);

		TokenJwtDTO result = this.userService.signIn("username", "password");
		assertNotNull(result);
		assertEquals("tokenValue", result.tokenJWT());
	}

	@Test
	void testUpdateUserPassword_Successful() {
		String rawOldPassword = "oldPassword";
		String rawNewPassword = "newPassword";
		String encryptedOldPassword = BCryptEncoder.encrypt(rawOldPassword);
		User user = new User("username", encryptedOldPassword, "user@example.com");

		when(this.userRepository.getReferenceByUsernameAndEmail(anyString(), anyString())).thenReturn(user);
		doNothing().when(this.userRepository).setPasswordWhereByUsername(anyString(), anyString());

		this.userService.updateUserPassword("username", "user@example.com", rawOldPassword, rawNewPassword);

		verify(this.userRepository).setPasswordWhereByUsername(eq("username"), anyString());
	}
}
