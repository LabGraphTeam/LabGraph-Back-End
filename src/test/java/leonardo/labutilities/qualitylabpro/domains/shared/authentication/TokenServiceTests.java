package leonardo.labutilities.qualitylabpro.domains.shared.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.auth0.jwt.exceptions.JWTVerificationException;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

@ExtendWith(SpringExtension.class)
class TokenServiceTests {

	@InjectMocks
	private TokenService tokenService;

	private static final String SECRET = "test-secret-key-12345";
	private static final String ISSUER = "quality-lab-pro";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(this.tokenService, "secret", SECRET);
		ReflectionTestUtils.setField(this.tokenService, "issuer", ISSUER);
	}

	@Test
	void generateToken_ValidUser_ReturnsValidToken() {
		// Arrange
		User user = new User();
		user.setUsername("testuser");

		// Act
		var tokenDto = this.tokenService.generateToken(user);

		// Assert
		assertNotNull(tokenDto);
		assertNotNull(tokenDto.tokenJWT());
		assertNotNull(tokenDto.dateExp());

		String subject = this.tokenService.getSubject(tokenDto.tokenJWT());
		assertEquals("testuser", subject);
	}

	@Test
	void getSubject_ValidToken_ReturnsCorrectSubject() {
		// Arrange
		User user = new User();
		user.setUsername("testuser");
		var tokenDto = this.tokenService.generateToken(user);

		// Act
		String subject = this.tokenService.getSubject(tokenDto.tokenJWT());

		// Assert
		assertEquals("testuser", subject);
	}

	@Test
	void getSubject_InvalidToken_ThrowsJWTVerificationException() {
		// Arrange
		String invalidToken = "invalid.token.string";

		// Act & Assert
		assertThrows(JWTVerificationException.class, () -> this.tokenService.getSubject(invalidToken));
	}

	@Test
	void getSubject_ExpiredToken_ThrowsJWTVerificationException() {
		// Arrange
		String expiredToken = """
				eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.\
				eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxNTE2MjM5MDIyfQ.\
				2hDgYN_KQy8R7UGxJXnsXtbJkO8JfHxFu0BbUAqVYsA""";

		// Act & Assert
		assertThrows(JWTVerificationException.class, () -> this.tokenService.getSubject(expiredToken));
	}
}
