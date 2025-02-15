package leonardo.labutilities.qualitylabpro.utils.constants;

import java.util.List;

public final class AvailableHematologyAnalytics {
	public static final List<String> DEFAULT_HEMATO_ANALYTICS = List.of("WBC", "RBC", "HGB", "HCT",
			"MCV", "MCH", "MCHC", "RDW-CV", "PLT", "NEU#", "LYM#", "MON#", "EOS#", "BAS#", "IMG#",
			"NRBC%", "NRBC#", "NEU%", "LYM%", "MON%", "EOS%", "BAS%", "IMG%");

	private AvailableHematologyAnalytics() {
		throw new UnsupportedOperationException("Cannot instantiate AvailableHematologyAnalytics");
	}
}
