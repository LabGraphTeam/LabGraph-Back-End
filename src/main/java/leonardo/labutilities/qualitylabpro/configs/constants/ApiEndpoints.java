package leonardo.labutilities.qualitylabpro.configs.constants;

public final class ApiEndpoints {

	public static final String COAGULATION_ANALYTICS_PATH = "/coagulation-analytics/**";
	public static final String HEMATOLOGY_ANALYTICS_PATH = "/hematology-analytics/**";
	public static final String BIOCHEMISTRY_ANALYTICS_PATH = "/biochemistry-analytics/**";
	public static final String GENERIC_ANALYTICS_PATH = "/generic-analytics/**";
	public static final String USERS_PATH = "/users/**";
	public static final String PASSWORD_PATH = "/users/password/**";
	public static final String RECOVER_PASSWORD_PATH = "/users/password/recover";


	public static final String SIGN_IN_PATH = "/users/sign-in";
	public static final String SIGN_UP_PATH = "/users/sign-up";

	private ApiEndpoints() {
		// Private constructor to prevent instantiation
	}
}
