package leonardo.labutilities.qualitylabpro.domains.analytics.utils;

import java.util.List;

import leonardo.labutilities.qualitylabpro.domains.analytics.constants.ThresholdAnalyticsRules;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.ValidationAnalyticsDescriptions;
import lombok.Getter;

@Getter
public final class SpecsValidatorUtility {

	private String description;
	private String rules;

	public void validator(final double value, final double mean, final double sd) {

		final List<Double> thresholds =
				List.of(mean + sd, mean + 2 * sd, mean + 3 * sd, mean - sd, mean - 2 * sd, mean - 3 * sd);
		final List<String> thresholdRules = ThresholdAnalyticsRules.RULES;

		final List<String> descriptions = ValidationAnalyticsDescriptions.DESCRIPTIONS;

		for (int i = 2; i >= 0; i--) {
			if (value >= thresholds.get(i) || value <= thresholds.get(i + 3)) {
				if (value >= thresholds.get(i)) {
					this.description = descriptions.get(i);
					this.rules = thresholdRules.get(i);
				}

				if (value <= thresholds.get(i + 3)) {
					this.description = descriptions.get(i + 3);
					this.rules = thresholdRules.get(i + 3);
				}

				return;
			}
		}
		this.description = "Approved according to current Westgard configured rules";
		this.rules = "No rule broken";
	}
}
