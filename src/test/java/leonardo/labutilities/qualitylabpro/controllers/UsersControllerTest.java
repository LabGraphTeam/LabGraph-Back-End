package leonardo.labutilities.qualitylabpro.controllers;

import leonardo.labutilities.qualitylabpro.configs.TestSecurityConfig;
import leonardo.labutilities.qualitylabpro.controllers.users.UsersController;
import leonardo.labutilities.qualitylabpro.dtos.authentication.LoginUserRecord;
import leonardo.labutilities.qualitylabpro.dtos.authentication.TokenJwtRecord;
import leonardo.labutilities.qualitylabpro.dtos.users.RecoverPasswordRecord;
import leonardo.labutilities.qualitylabpro.dtos.users.UpdatePasswordRecord;
import leonardo.labutilities.qualitylabpro.dtos.users.UsersRecord;
import leonardo.labutilities.qualitylabpro.entities.User;
import leonardo.labutilities.qualitylabpro.enums.UserRoles;
import leonardo.labutilities.qualitylabpro.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.services.authentication.TokenService;
import leonardo.labutilities.qualitylabpro.services.users.UserService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private JacksonTester<UsersRecord> usersRecordJacksonTester;

    @Autowired
    private JacksonTester<LoginUserRecord> loginRecordJacksonTester;

    @Autowired
    private JacksonTester<UpdatePasswordRecord> updatePasswordRecordJacksonTester;

    @Autowired
    private JacksonTester<RecoverPasswordRecord> recoverPasswordRecordJacksonTester;

    @Test
    @DisplayName("Should return 204 when signing up a new user")
    void signUp_return_204() throws Exception {
        UsersRecord usersRecord = new UsersRecord("testUser", "Marmota2024@", "test@example.com");
        User user = new User("testUser", "password", "test@example.com", UserRoles.USER);

        when(userService.signUp(anyString(), anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/users/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(usersRecordJacksonTester.write(usersRecord).getJson()))
               .andExpect(status().isNoContent())
               .andExpect(jsonPath("$.username").value("testUser"))
               .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).signUp(usersRecord.username(), usersRecord.password(), usersRecord.email());
    }

    @Test
    @DisplayName("Should return 200 and call userService.signIn when signing in")
    void signIn_shouldReturn200AndCallUserService() throws Exception {
        // Arrange
        LoginUserRecord loginRecord = new LoginUserRecord("test@example.com", "password");
        TokenJwtRecord tokenJwtRecord = new TokenJwtRecord("TokenJwt");

        when(userService.signIn(loginRecord.email(), loginRecord.password()))
                .thenReturn(tokenJwtRecord);

        mockMvc.perform(post("/users/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRecordJacksonTester
                                                 .write(loginRecord)
                                                 .getJson())).andExpect(status().isOk())
               .andExpect(jsonPath("$.tokenJWT").value("TokenJwt"));

        verify(userService).signIn("test@example.com", "password");

    }

    @Test
    @DisplayName("Should return 204 when requesting password recovery")
    void forgotPassword_return_204() throws Exception {
        UsersRecord usersRecord = new UsersRecord("testUser", "Mandrake2024@", "test@example.com");

        mockMvc.perform(post("/users/password/forgot-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(usersRecordJacksonTester.write(usersRecord).getJson()))
               .andExpect(status().isNoContent());

        verify(userService).recoverPassword(usersRecord.username(), usersRecord.email());
    }

    @Test
    @DisplayName("Should return 204 when changing password with recovery token")
    void changePassword_return_204() throws Exception {
        RecoverPasswordRecord recoverRecord = new RecoverPasswordRecord(
                "test@example.com", "tempPassword", "newPassword");

        mockMvc.perform(patch("/users/password/recover")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(recoverPasswordRecordJacksonTester.write(recoverRecord).getJson()))
               .andExpect(status().isNoContent());

        verify(userService).changePassword(
                recoverRecord.email(),
                recoverRecord.temporaryPassword(),
                recoverRecord.newPassword());
    }

    @Test
    @DisplayName("Should return 204 when updating password for authenticated user")
    @WithMockUser
    void updatePassword_return_204() throws Exception {
        User user = new User("testUser", "oldPassword", "test@example.com", UserRoles.USER);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        final var auth = (User) authentication.getPrincipal();

        UpdatePasswordRecord updateRecord = new UpdatePasswordRecord
                (auth.getUsername(), auth.getEmail(), "oldPassword", "newPassword");

        mockMvc.perform(patch("/users/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePasswordRecordJacksonTester.write(updateRecord).getJson())
                                .with(SecurityMockMvcRequestPostProcessors.user(user)))
               .andExpect(status().isNoContent());

        verify(userService).updateUserPassword(
                updateRecord.username(),
                updateRecord.email(),
                updateRecord.oldPassword(),
                updateRecord.newPassword()
        );
    }

    @Test
    @DisplayName("Should return 400 when signing up with invalid data")
    void signUp_with_invalid_data_return_400() throws Exception {
        UsersRecord invalidRecord = new UsersRecord("", "", "invalid-email");

        mockMvc.perform(post("/users/sign-up")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(usersRecordJacksonTester.write(invalidRecord).getJson()))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 401 when signing in with invalid credentials")
    void signIn_with_invalid_credentials_return_401() throws Exception {
        LoginUserRecord loginRecord = new LoginUserRecord("test@example.com", "wrongpassword");

        when(userService.signIn(any(), any()))
                .thenThrow(new BadCredentialsException("Authentication failed at"));

        mockMvc.perform(post("/users/sign-in")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRecordJacksonTester.write(loginRecord).getJson()))
               .andExpect(status().isUnauthorized());

        verify(userService, times(1)).signIn(loginRecord.email(), loginRecord.password());
    }
}