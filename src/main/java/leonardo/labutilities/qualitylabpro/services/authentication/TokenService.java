package leonardo.labutilities.qualitylabpro.services.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import leonardo.labutilities.qualitylabpro.dtos.authentication.TokenJwtDTO;
import leonardo.labutilities.qualitylabpro.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TokenService {

	@Value("${api.security.token.secret}")
	private String SECRET;

	@Value("${api.security.issuer}")
	private String ISSUER;

	public TokenJwtDTO generateToken(User user) {
		try {
			var algorithm = Algorithm.HMAC256(SECRET);
			return new TokenJwtDTO(JWT.create().withIssuer(ISSUER).withSubject(user.getEmail())
					.withExpiresAt(dateExp()).sign(algorithm), dateExp());
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
		return LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant();
	}
}
