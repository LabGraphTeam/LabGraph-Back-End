package leonardo.labutilities.qualitylabpro.utils.constants;

import java.util.List;

public record AvailableCoagulationAnalytics(List<String> availableCoagulationAnalytics) {
	private static final List<String> DEFAULT_COAG_ANALYTICS = List.of("TAP-20", "TTPA");

	public AvailableCoagulationAnalytics() {
		this(DEFAULT_COAG_ANALYTICS);
	}
}
