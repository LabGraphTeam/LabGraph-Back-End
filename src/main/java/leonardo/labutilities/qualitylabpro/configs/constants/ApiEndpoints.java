package leonardo.labutilities.qualitylabpro.configs.constants;

public final class ApiEndpoints {

	private static final String COAGULATION_ANALYTICS_PATH = "/coagulation-analytics/**";
	private static final String HEMATOLOGY_ANALYTICS_PATH = "/hematology-analytics/**";
	private static final String BIOCHEMISTRY_ANALYTICS_PATH = "/biochemistry-analytics/**";
	private static final String GENERIC_ANALYTICS_PATH = "/generic-analytics/**";
	public static final String EQUIPMENT_PATH = "/equipment/**";
	public static final String CONTROL_LOT_PATH = "/control-lot/**";

	public static final String USERS_PATH = "/users/**";
	public static final String PASSWORD_PATH = "/users/password/**";

	private static final String SIGN_IN_PATH = "/users/sign-in";
	private static final String SIGN_UP_PATH = "/users/sign-up";

	public static final String[] PUBLIC_PATHS =
			{"/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/actuator/**"};

	public static final String[] PUBLIC_POST_PATHS =
			{ApiEndpoints.SIGN_IN_PATH, ApiEndpoints.USERS_PATH, ApiEndpoints.PASSWORD_PATH};

	public static final String[] ADMIN_MODIFY_PATHS = {ApiEndpoints.GENERIC_ANALYTICS_PATH,
			ApiEndpoints.BIOCHEMISTRY_ANALYTICS_PATH, ApiEndpoints.HEMATOLOGY_ANALYTICS_PATH,
			ApiEndpoints.COAGULATION_ANALYTICS_PATH, ApiEndpoints.SIGN_UP_PATH, ApiEndpoints.EQUIPMENT_PATH,
			ApiEndpoints.CONTROL_LOT_PATH};

	private ApiEndpoints() {}
}
