package leonardo.labutilities.qualitylabpro.domains.shared.authentication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.dtos.responses.TokenJwtDTO;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

@Service
public class TokenService {

	@Value("${api.security.token.secret}")
	private String secret;

	@Value("${api.security.issuer}")
	private String issuer;

	public TokenJwtDTO generateToken(User user) {
		try {
			var algorithm = Algorithm.HMAC256(this.secret);
			return new TokenJwtDTO(JWT.create().withIssuer(this.issuer)
					.withSubject(user.getUsername()).withExpiresAt(dateExp()).sign(algorithm),
					dateExp());
		} catch (JWTCreationException exception) {
			throw new JWTCreationException("Error generating token", exception);
		}
	}

	public String getSubject(String tokenJWT) {
		try {
			var algorithm = Algorithm.HMAC256(this.secret);
			return JWT.require(algorithm).withIssuer(this.issuer).build().verify(tokenJWT)
					.getSubject();
		} catch (JWTVerificationException exception) {
			throw new JWTVerificationException("Invalid token: " + exception.getMessage(),
					exception);
		}
	}

	private static Instant dateExp() {
		return LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant();
	}
}
