package leonardo.labutilities.qualitylabpro.domains.users.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PasswordRecoveryTokenManagerTests {

    private PasswordRecoveryTokenManager tokenManager;

    @BeforeEach
    void setUp() {
        tokenManager = new PasswordRecoveryTokenManager();
    }

    @Test
    @DisplayName("Should generate temporary password of expected format")
    void generateTemporaryPassword_ShouldReturnBase64EncodedString() {
        // Act
        String temporaryPassword = tokenManager.generateTemporaryPassword();

        // Assert
        assertNotNull(temporaryPassword);
        assertFalse(temporaryPassword.isEmpty());
        // Base64 URL encoding creates alphanumeric string with possibly '-' and '_'
        assertTrue(temporaryPassword.matches("^[A-Za-z0-9\\-_]+$"));
    }

    @Test
    @DisplayName("Should generate and store token successfully")
    void generateAndStoreToken_ShouldStoreEmailAndReturnToken() {
        // Arrange
        String email = "user@example.com";

        // Act
        String token = tokenManager.generateAndStoreToken(email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(tokenManager.isRecoveryTokenValid(token, email));
    }

    @Test
    @DisplayName("Token validation should return true for valid token and email")
    void isRecoveryTokenValid_WithValidTokenAndEmail_ShouldReturnTrue() {
        // Arrange
        String email = "user@example.com";
        String token = tokenManager.generateAndStoreToken(email);

        // Act
        boolean isValid = tokenManager.isRecoveryTokenValid(token, email);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Token validation should return false for valid token but wrong email")
    void isRecoveryTokenValid_WithValidTokenButWrongEmail_ShouldReturnFalse() {
        // Arrange
        String correctEmail = "correct@example.com";
        String wrongEmail = "wrong@example.com";
        String token = tokenManager.generateAndStoreToken(correctEmail);

        // Act
        boolean isValid = tokenManager.isRecoveryTokenValid(token, wrongEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Token validation should return false for invalid token")
    void isRecoveryTokenValid_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String email = "user@example.com";
        String invalidToken = "invalid_token";

        // Act
        boolean isValid = tokenManager.isRecoveryTokenValid(invalidToken, email);

        // Assert
        assertFalse(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"user1@example.com", "user2@example.com", "user3@example.com"})
    @DisplayName("Multiple users should get different tokens")
    void generateAndStoreToken_WithMultipleUsers_ShouldGenerateUniqueTokens(String email) {
        // Act
        String token1 = tokenManager.generateAndStoreToken(email);
        String token2 = tokenManager.generateAndStoreToken(email);

        // Assert
        assertNotEquals(token1, token2, "Tokens should be different even for the same email");
        assertTrue(tokenManager.isRecoveryTokenValid(token2, email),
                "The latest token should be valid");
    }

    @Test
    @DisplayName("Generated tokens should be of expected format")
    void generateAndStoreToken_ShouldReturnBase64EncodedToken() {
        // Arrange
        String email = "user@example.com";

        // Act
        String token = tokenManager.generateAndStoreToken(email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // Base64 URL encoding creates alphanumeric string with possibly '-' and '_'
        assertTrue(token.matches("^[A-Za-z0-9\\-_]+$"));
    }
}
