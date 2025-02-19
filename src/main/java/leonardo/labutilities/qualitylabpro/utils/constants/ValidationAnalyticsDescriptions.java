package leonardo.labutilities.qualitylabpro.utils.constants;

import java.util.List;

public final class ValidationAnalyticsDescriptions {

	public static final List<String> DESCRIPTIONS = List.of(
		"Within acceptable positive range (+1s)",
		"Within acceptable positive range (+2s)", 
		"Rule violation: exceeded positive limit (+3s)",
		"Within acceptable negative range (-1s)",
		"Within acceptable negative range (-2s)", 
		"Rule violation: exceeded negative limit (-3s)"
	);
	private ValidationAnalyticsDescriptions() {}

}
