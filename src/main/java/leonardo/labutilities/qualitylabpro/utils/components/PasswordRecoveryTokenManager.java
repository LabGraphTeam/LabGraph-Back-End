package leonardo.labutilities.qualitylabpro.utils.components;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PasswordRecoveryTokenManager {
	private static final HashMap<String, String> stringHashMap = new HashMap<>();

	public String generateTemporaryPassword() {
		return java.util.UUID.randomUUID().toString();
	}

	public void generateAndStoreToken(String email, String password) {
		String hashedPassword = generateRecoveryToken(email, password);
		stringHashMap.put(hashedPassword, email);
	}

	public String retrieveEmailFromToken(String hashedPassword) {
		return stringHashMap.get(hashedPassword);
	}

	public boolean isRecoveryTokenValid(String token, String email) {
		return stringHashMap.get(token).equals(email);
	}

	private static String generateRecoveryToken(String email, String password) {
		return BCryptEncoderComponent.encrypt(email + password);
	}
}
