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

	public static final String[] PUBLIC_PATHS =
			{"/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/actuator/**"};

	public static final String[] PUBLIC_POST_PATHS = {ApiEndpoints.SIGN_IN_PATH,
			ApiEndpoints.SIGN_UP_PATH, ApiEndpoints.HEMATOLOGY_ANALYTICS_PATH,
			ApiEndpoints.USERS_PATH, ApiEndpoints.PASSWORD_PATH};

	public static final String[] ADMIN_MODIFY_PATHS = {ApiEndpoints.GENERIC_ANALYTICS_PATH,
			ApiEndpoints.BIOCHEMISTRY_ANALYTICS_PATH, ApiEndpoints.HEMATOLOGY_ANALYTICS_PATH,
			ApiEndpoints.COAGULATION_ANALYTICS_PATH};

	private ApiEndpoints() {}
}
