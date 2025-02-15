package leonardo.labutilities.qualitylabpro.utils.constants;

import java.util.List;

public final class AvailableCoagulationAnalytics {
	public static final List<String> DEFAULT_COAG_ANALYTICS = List.of("TAP-20", "TTPA");

	private AvailableCoagulationAnalytics() {
		throw new UnsupportedOperationException("Cannot instantiate AvailableCoagulationAnalytics");
	}
}
