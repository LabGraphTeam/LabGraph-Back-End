package leonardo.labutilities.qualitylabpro.utils.constants;

import java.util.List;

public record AvailableHematologyAnalytics(List<String> availableHematologyAnalytics) {
	private static final List<String> DEFAULT_HEMATO_ANALYTICS = List.of("WBC", "RBC", "HGB", "HCT",
			"MCV", "MCH", "MCHC", "RDW-CV", "PLT", "NEU#", "LYM#", "MON#", "EOS#", "BAS#", "IMG#",
			"NRBC%", "NRBC#", "NEU%", "LYM%", "MON%", "EOS%", "BAS%", "IMG%");

	public AvailableHematologyAnalytics() {
		this(DEFAULT_HEMATO_ANALYTICS);
	}
}
