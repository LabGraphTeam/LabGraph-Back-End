package leonardo.labutilities.qualitylabpro.domains.users.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class BCryptEncoder {

	private BCryptEncoder() {}

	public static Boolean decrypt(String pass, String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.matches(pass, password);
	}

	public static String encrypt(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(password);
	}
}
