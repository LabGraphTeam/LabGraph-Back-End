package leonardo.labutilities.qualitylabpro.services.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import leonardo.labutilities.qualitylabpro.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

	@Value("${API_SECURITY_TOKEN_SECRET}")
	private String SECRET;
	@Value("${API_SECURITY_ISSUER}")
	private String ISSUER;

	public String generateToken(User user) {
		try {
			var algorithm = Algorithm.HMAC256(SECRET);
			return JWT.create().withIssuer(ISSUER).withSubject(user.getEmail())
					.withExpiresAt(dateExp()).sign(algorithm);
		} catch (JWTCreationException exception) {
			throw new RuntimeException("Error generating token", exception);
		}
	}

	public String getSubject(String tokenJWT) {
		try {
			var algorithm = Algorithm.HMAC256(SECRET);
			return JWT.require(algorithm).withIssuer(ISSUER).build().verify(tokenJWT).getSubject();
		} catch (JWTVerificationException exception) {
			throw new JWTVerificationException("Invalid token: " + exception.getMessage(),
					exception);
		}
	}

	private Instant dateExp() {
		return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
	}
}
