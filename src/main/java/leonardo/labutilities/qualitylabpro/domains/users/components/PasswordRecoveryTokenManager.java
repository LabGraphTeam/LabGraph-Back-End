package leonardo.labutilities.qualitylabpro.domains.users.components;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class PasswordRecoveryTokenManager {
	private static final HashMap<String, String> stringHashMap = new HashMap<>();
	private static final SecureRandom secureRandom = new SecureRandom();

	public String generateTemporaryPassword() {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(new byte[16]);
	}

	public String generateAndStoreToken(final String email) {
		final String token = generateRecoveryToken();
		stringHashMap.put(token, email);
		return token;
	}

	public boolean isRecoveryTokenValid(final String token, final String email) {
		final String storedEmail = stringHashMap.get(token);
		if (storedEmail == null) {
			return false;
		}
		return storedEmail.equals(email);
	}

	private static String generateRecoveryToken() {
		byte[] tokenBytes = new byte[32];
		secureRandom.nextBytes(tokenBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
	}
}
