package leonardo.labutilities.qualitylabpro.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import leonardo.labutilities.qualitylabpro.configs.TestSecurityConfig;
import leonardo.labutilities.qualitylabpro.controllers.users.UsersController;
import leonardo.labutilities.qualitylabpro.dtos.authentication.LoginUserDTO;
import leonardo.labutilities.qualitylabpro.dtos.authentication.TokenJwtDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.RecoverPasswordDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.SignUpUsersDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.UpdatePasswordDTO;
import leonardo.labutilities.qualitylabpro.dtos.users.UsersDTO;
import leonardo.labutilities.qualitylabpro.entities.User;
import leonardo.labutilities.qualitylabpro.enums.UserRoles;
import leonardo.labutilities.qualitylabpro.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.services.authentication.TokenService;
import leonardo.labutilities.qualitylabpro.services.users.UserService;

@WebMvcTest(UsersController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
class UsersControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private AuthenticationManager authenticationManager;

	@MockitoBean
	private TokenService tokenService;

	@MockitoBean
	private UserRepository userRepository;

	@Autowired
	private JacksonTester<UsersDTO> usersRecordJacksonTester;

	@Autowired
	private JacksonTester<SignUpUsersDTO> signUpUsersDTOJacksonTester;


	@Autowired
	private JacksonTester<LoginUserDTO> loginRecordJacksonTester;

	@Autowired
	private JacksonTester<UpdatePasswordDTO> updatePasswordRecordJacksonTester;

	@Autowired
	private JacksonTester<RecoverPasswordDTO> recoverPasswordRecordJacksonTester;

	@Test
	@DisplayName("Should return 204 when signing up a new user")
	void signUp_return_204() throws Exception {
		SignUpUsersDTO signUpUsersDTO =
				new SignUpUsersDTO("Leonardo Meireles", "marmotas2024@email.com", "marmotas2024@");

		mockMvc.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON)
				.content(signUpUsersDTOJacksonTester.write(signUpUsersDTO).getJson()))
				.andExpect(status().isNoContent());

		verify(userService).signUp(signUpUsersDTO.identifier(), signUpUsersDTO.email(),
				signUpUsersDTO.password());
	}

	@Test
	@DisplayName("Should return 200 and call userService.signIn when signing in")
	void signIn_shouldReturn200AndCallUserService() throws Exception {
		Instant dateExp = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-00:00"));
		// Arrange
		LoginUserDTO loginRecord = new LoginUserDTO("test@example.com", "password");
		TokenJwtDTO tokenJwtDTO = new TokenJwtDTO("TokenJwt", dateExp);

		when(userService.signIn(loginRecord.identifier(), loginRecord.password()))
				.thenReturn(tokenJwtDTO);

		mockMvc.perform(post("/users/sign-in").contentType(MediaType.APPLICATION_JSON)
				.content(loginRecordJacksonTester.write(loginRecord).getJson()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.tokenJWT").value("TokenJwt"));

		verify(userService).signIn("test@example.com", "password");

	}

	@Test
	@DisplayName("Should return 204 when requesting password recovery")
	void forgotPassword_return_204() throws Exception {
		UsersDTO usersDTO = new UsersDTO("testUser", "Mandrake2024@", "test@example.com");

		mockMvc.perform(
				post("/users/password/forgot-password").contentType(MediaType.APPLICATION_JSON)
						.content(usersRecordJacksonTester.write(usersDTO).getJson()))
				.andExpect(status().isNoContent());

		verify(userService).recoverPassword(usersDTO.username(), usersDTO.email());
	}

	@Test
	@DisplayName("Should return 204 when changing password with recovery token")
	void changePassword_return_204() throws Exception {
		RecoverPasswordDTO recoverRecord =
				new RecoverPasswordDTO("test@example.com", "tempPassword", "newPassword");

		mockMvc.perform(patch("/users/password/recover").contentType(MediaType.APPLICATION_JSON)
				.content(recoverPasswordRecordJacksonTester.write(recoverRecord).getJson()))
				.andExpect(status().isNoContent());

		verify(userService).changePassword(recoverRecord.email(), recoverRecord.temporaryPassword(),
				recoverRecord.newPassword());
	}

	@Test
	@DisplayName("Should return 204 when updating password for authenticated user")
	@WithMockUser
	void updatePassword_return_204() throws Exception {
		User user = new User("testUser", "oldPassword", "test@example.com", UserRoles.USER);

		Authentication authentication =
				new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		SecurityContextHolder.setContext(securityContext);
		final var auth = (User) authentication.getPrincipal();

		UpdatePasswordDTO updateRecord = new UpdatePasswordDTO(auth.getUsername(), auth.getEmail(),
				"oldPassword", "newPassword");

		mockMvc.perform(patch("/users/password").contentType(MediaType.APPLICATION_JSON)
				.content(updatePasswordRecordJacksonTester.write(updateRecord).getJson())
				.with(SecurityMockMvcRequestPostProcessors.user(user)))
				.andExpect(status().isNoContent());

		verify(userService).updateUserPassword(updateRecord.username(), updateRecord.email(),
				updateRecord.oldPassword(), updateRecord.newPassword());
	}

	@Test
	@DisplayName("Should return 400 when signing up with invalid data")
	void signUp_with_invalid_data_return_400() throws Exception {
		UsersDTO invalidRecord = new UsersDTO("", "", "invalid-identifier");

		mockMvc.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON)
				.content(usersRecordJacksonTester.write(invalidRecord).getJson()))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Should return 401 when signing in with invalid credentials")
	void signIn_with_invalid_credentials_return_401() throws Exception {
		LoginUserDTO loginRecord = new LoginUserDTO("test@example.com", "wrongpassword");

		when(userService.signIn(any(), any()))
				.thenThrow(new BadCredentialsException("Authentication failed at"));

		mockMvc.perform(post("/users/sign-in").contentType(MediaType.APPLICATION_JSON)
				.content(loginRecordJacksonTester.write(loginRecord).getJson()))
				.andExpect(status().isUnauthorized());

		verify(userService, times(1)).signIn(loginRecord.identifier(), loginRecord.password());
	}
}
