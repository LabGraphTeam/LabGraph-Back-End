package leonardo.labutilities.qualitylabpro.domains.users.controllers;

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
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.dtos.TokenJwtDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.services.TokenService;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.ForgotPasswordDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.SignInUserDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.RecoverPasswordDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.SignUpUsersDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.requests.UpdatePasswordDTO;
import leonardo.labutilities.qualitylabpro.domains.users.dtos.responses.UsersDTO;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import leonardo.labutilities.qualitylabpro.domains.users.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.domains.users.services.UserService;

@WebMvcTest(UsersController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
class UsersControllerTests {

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
	private JacksonTester<ForgotPasswordDTO> forgotPasswordRecordJacksonTester;

	@Autowired
	private JacksonTester<SignUpUsersDTO> signUpUsersDTOJacksonTester;

	@Autowired
	private JacksonTester<SignInUserDTO> loginRecordJacksonTester;

	@Autowired
	private JacksonTester<UpdatePasswordDTO> updatePasswordRecordJacksonTester;

	@Autowired
	private JacksonTester<RecoverPasswordDTO> recoverPasswordRecordJacksonTester;

	@Test
	@DisplayName("Should return no content when successfully registering a new user")
	void signUp_return_204() throws Exception {
		SignUpUsersDTO signUpUsersDTO =
				new SignUpUsersDTO("Leonardo Meireles", "marmotas2024@email.com", "marmotas2024@");

		this.mockMvc
				.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON)
						.content(this.signUpUsersDTOJacksonTester.write(signUpUsersDTO).getJson()))
				.andExpect(status().isCreated());

		verify(this.userService).signUp(signUpUsersDTO.identifier(), signUpUsersDTO.email(),
				signUpUsersDTO.password());
	}

	@Test
	@DisplayName("Should return token when user credentials are valid")
	void signIn_shouldReturn200AndCallUserService() throws Exception {
		Instant dateExp = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-00:00"));
		SignInUserDTO loginRecord = new SignInUserDTO("test@example.com", "password");
		TokenJwtDTO tokenJwtDTO = new TokenJwtDTO("TokenJwt", dateExp);

		when(this.userService.signIn(loginRecord.identifier(), loginRecord.password()))
				.thenReturn(tokenJwtDTO);

		this.mockMvc
				.perform(post("/users/sign-in").contentType(MediaType.APPLICATION_JSON)
						.content(this.loginRecordJacksonTester.write(loginRecord).getJson()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.tokenJWT").value("TokenJwt"));

		verify(this.userService).signIn("test@example.com", "password");

	}

	@Test
	@DisplayName("Should return no content when password recovery request is valid")
	void forgotPassword_return_204() throws Exception {
		ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO("testUser", "test@example.com");

		this.mockMvc
				.perform(
						post("/users/password/forgot-password")
								.contentType(MediaType.APPLICATION_JSON)
								.content(this.forgotPasswordRecordJacksonTester
										.write(forgotPasswordDTO).getJson()))
				.andExpect(status().isNoContent());

		verify(this.userService).recoverPassword(forgotPasswordDTO.username(),
				forgotPasswordDTO.email());
	}

	@Test
	@DisplayName("Should return no content when password is changed with valid recovery token")
	void changePassword_return_204() throws Exception {
		RecoverPasswordDTO recoverRecord =
				new RecoverPasswordDTO("test@example.com", "tempPassword", "newPass@123");

		this.mockMvc
				.perform(
						patch("/users/password/recover").contentType(MediaType.APPLICATION_JSON)
								.content(this.recoverPasswordRecordJacksonTester
										.write(recoverRecord).getJson()))
				.andExpect(status().isNoContent());

		verify(this.userService).changePassword(recoverRecord.email(),
				recoverRecord.temporaryPassword(), recoverRecord.newPassword());
	}

	@Test
	@DisplayName("Should return no content when authenticated user updates password")
	@WithMockUser
	void updatePassword_return_204() throws Exception {
		User user = new User("testUser", "oldPassword", "test@example.com");

		Authentication authentication =
				new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		SecurityContextHolder.setContext(securityContext);
		final var auth = (User) authentication.getPrincipal();

		UpdatePasswordDTO updateRecord = new UpdatePasswordDTO(auth.getUsername(), auth.getEmail(),
				"oldPassword", "newPassword");

		this.mockMvc
				.perform(patch("/users/password").contentType(MediaType.APPLICATION_JSON)
						.content(this.updatePasswordRecordJacksonTester.write(updateRecord)
								.getJson())
						.with(SecurityMockMvcRequestPostProcessors.user(user)))
				.andExpect(status().isNoContent());

		verify(this.userService).updateUserPassword(updateRecord.username(), updateRecord.email(),
				updateRecord.oldPassword(), updateRecord.newPassword());
	}

	@Test
	@DisplayName("Should return bad request when signup data is invalid")
	void signUp_with_invalid_data_return_400() throws Exception {
		UsersDTO invalidRecord = new UsersDTO("", "", "invalid-identifier");

		this.mockMvc
				.perform(post("/users/sign-up").contentType(MediaType.APPLICATION_JSON)
						.content(this.usersRecordJacksonTester.write(invalidRecord).getJson()))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Should return unauthorized when login credentials are invalid")
	void signIn_with_invalid_credentials_return_401() throws Exception {
		SignInUserDTO loginRecord = new SignInUserDTO("test@example.com", "wrongpassword");

		when(this.userService.signIn(any(), any()))
				.thenThrow(new BadCredentialsException("Authentication failed at"));

		this.mockMvc
				.perform(post("/users/sign-in").contentType(MediaType.APPLICATION_JSON)
						.content(this.loginRecordJacksonTester.write(loginRecord).getJson()))
				.andExpect(status().isUnauthorized());

		verify(this.userService, times(1)).signIn(loginRecord.identifier(), loginRecord.password());
	}
}
