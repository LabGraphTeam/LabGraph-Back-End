package leonardo.labutilities.qualitylabpro.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import leonardo.labutilities.qualitylabpro.entities.User;
import leonardo.labutilities.qualitylabpro.enums.UserRoles;
import leonardo.labutilities.qualitylabpro.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.services.users.UserService;
import leonardo.labutilities.qualitylabpro.utils.components.BCryptEncoderComponent;
import leonardo.labutilities.qualitylabpro.utils.components.PasswordRecoveryTokenManager;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;

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

        userService.recoverPassword("username", "email@example.com");

        verify(passwordRecoveryTokenManager).generateAndStoreToken("email@example.com", "tempPassword");
        verify(emailService).sendEmail(any());
    }

    @Test
    void testRecoverPassword_UserDoesNotExist() {
        when(userRepository.existsByUsernameAndEmail(anyString(), anyString())).thenReturn(false);

        assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class, () -> userService.recoverPassword("username", "email@example.com"));
    }

    @Test
    void testChangePassword_ValidToken() {
        User user = new User("username", BCryptEncoderComponent.encrypt("newPassword"), "email@example.com", UserRoles.USER);
        when(passwordRecoveryTokenManager.isRecoveryTokenValid(anyString(), anyString())).thenReturn(true);
        userService.changePassword("email@example.com", "tempPassword", "newPassword");
        assertThat(passwordRecoveryTokenManager.isRecoveryTokenValid("tempPassword", "email@example.com")).isTrue();
    }

    @Test
    void testChangePassword_InvalidToken() {
        when(passwordRecoveryTokenManager.isRecoveryTokenValid(anyString(), anyString())).thenReturn(false);

        assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class, () -> userService.changePassword("email@example.com", "tempPassword", "newPassword"));
    }

    @Test
    void testSignUp_UserAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(CustomGlobalErrorHandling.UserAlreadyExistException.class, () -> userService.signUp("username", "password", "email@example.com"));
    }

    @Test
    void testSignUp_NewUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User("username", "encryptedPassword", "email@example.com", UserRoles.USER));

        User user = userService.signUp("username", "password", "email@example.com");

        assertNotNull(user);
        assertEquals("username", user.getUsername());
        assertEquals("email@example.com", user.getEmail());
    }

    @Test
    void should_return_error_with_testUpdateUserPassword_PasswordMatches() {
        User user = new User("username", BCryptEncoderComponent.encrypt("newPassword"), "email@example.com", UserRoles.USER);

        when(userRepository.getReferenceByUsernameAndEmail(anyString(), anyString())).thenReturn(user);

        assertThrows(CustomGlobalErrorHandling.PasswordNotMatchesException.class, () -> userService.updateUserPassword("username", "email@example.com", "oldPassword", "newPassword"));
        verify(userRepository, never()).setPasswordWhereByUsername(anyString(), anyString());
    }

    @Test
    void testUpdateUserPassword_PasswordDoesNotMatch() {
        User user = new User("username", BCryptEncoderComponent.encrypt("oldPassword"), "email@example.com", UserRoles.USER);
        when(userRepository.getReferenceByUsernameAndEmail(anyString(), anyString())).thenReturn(user);

        assertThrows(CustomGlobalErrorHandling.PasswordNotMatchesException.class, () -> userService.updateUserPassword("username", "email@example.com", "wrongPassword", "newPassword"));
    }
}