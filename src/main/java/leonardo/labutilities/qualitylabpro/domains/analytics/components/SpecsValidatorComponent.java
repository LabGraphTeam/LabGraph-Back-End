package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import java.util.List;

import org.springframework.stereotype.Component;

import leonardo.labutilities.qualitylabpro.domains.analytics.constants.ThresholdAnalyticsRules;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.ValidationAnalyticsDescriptions;
import lombok.Getter;

@Getter
@Component
public class SpecsValidatorComponent {

	private String description;
	private String rules;

	public void validator(Double value, Double mean, Double sd) {

		List<Double> thresholds = List.of(mean + sd, mean + 2 * sd, mean + 3 * sd, mean - sd,
				mean - 2 * sd, mean - 3 * sd);
		List<String> thresholdRules = ThresholdAnalyticsRules.RULES;

		List<String> descriptions = ValidationAnalyticsDescriptions.DESCRIPTIONS;

		for (int i = 2; i >= 0; i--) {
			if (value >= thresholds.get(i) || value <= thresholds.get(i + 3)) {
				this.description = descriptions.get(i);
				if (value >= thresholds.get(i)) {
					this.rules = thresholdRules.get(i);
				} else {
					this.rules = thresholdRules.get(i + 3);
				}
				return;
			}
		}
		this.description = "Approved according to current Westgard configured rules";
		this.rules = "No rule broken";
	}
}
