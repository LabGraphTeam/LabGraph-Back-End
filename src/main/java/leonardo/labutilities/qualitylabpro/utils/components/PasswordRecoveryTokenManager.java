package leonardo.labutilities.qualitylabpro.utils.components;

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

	public String generateAndStoreToken(String email) {
		String token = generateRecoveryToken();
		stringHashMap.put(token, email);
		return token;
	}

	public boolean isRecoveryTokenValid(String token, String email) {
		String storedEmail = stringHashMap.get(token);
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
