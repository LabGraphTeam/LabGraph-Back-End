package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import static leonardo.labutilities.qualitylabpro.domains.shared.email.constants.EmailTemplate.ERROR_MESSAGE_TEMPLATE;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.blacklist.AnalyticsBlackList;

@Component
public class RulesProviderComponent {

	private static final int RULE_3S_MULTIPLIER = 3;
	private static final int RULE_4_1S_CONSECUTIVE = 4;
	private static final int RULE_10X_CONSECUTIVE = 10;

	private final AnalyticsRepository analyticsRepository;

	public RulesProviderComponent(AnalyticsRepository analyticsRepository) {
		this.analyticsRepository = analyticsRepository;
	}

	public String validateRules(List<AnalyticsDTO> analytics) {
		if (analytics == null || analytics.isEmpty()) {
			return "<div style='font-family: Arial, sans-serif;'>No analytics data provided for validation.</div>";
		}
		StringBuilder errors = new StringBuilder();
		errors.append("<div style='font-family: Arial, sans-serif;'>");

		// Track already reported violations
		Set<String> reportedViolations = new HashSet<>();

		for (AnalyticsDTO analytic : analytics) {
			// Create unique key for test/level combination
			String violationKey = analytic.name() + "-" + analytic.level();

			// Skip if we already reported this violation
			if (reportedViolations.contains(violationKey)) {
				continue;
			}

			var analyticsRecords = this.analyticsRepository
					.findLast10ByTestNameAndControlLevel(analytic.name(), analytic.level()).stream()
					.filter(analyticsRecord -> !AnalyticsBlackList.BLACK_LIST
							.contains(analyticsRecord.name()))
					.toList();

			var mean = analyticsRecords.getFirst().mean();
			var stdDev = analyticsRecords.getFirst().sd();
			var values = analyticsRecords.stream().map(AnalyticsDTO::value).toList();

			var lastAnalyticsRecords = this.analyticsRepository
					.findLastByTestNameAndControlLevel(analytic.name(), analytic.level()).stream()
					.filter(analyticsEntry -> !AnalyticsBlackList.BLACK_LIST
							.contains(analyticsEntry.name()))
					.toList();

			var lastMean = lastAnalyticsRecords.getFirst().mean();
			var lastStdDev = lastAnalyticsRecords.getFirst().sd();
			var lastValues = lastAnalyticsRecords.stream().map(AnalyticsDTO::value).toList();

			if (this.oneThreeSigmaRule(lastValues, lastMean, lastStdDev)) {
				errors.append(String.format(ERROR_MESSAGE_TEMPLATE, "1-3s", analytic.name(),
						analytic.level(), "One observation exceeds mean ±3 SD",
						"Random Error. Reject run and investigate for potential systematic errors."));
				reportedViolations.add(violationKey);
			}

			if (this.fourOneSigmaRule(values, mean, stdDev)) {
				errors.append(String.format(ERROR_MESSAGE_TEMPLATE, "4-1s", analytic.name(),
						analytic.level(),
						"Four consecutive measurements exceed ±1 SD on same side of mean",
						"Systematic Error. Check for calibration drift, reagent lot changes, or environmental conditions."));
				reportedViolations.add(violationKey);
			}

			if (this.tenConsecutiveRule(values, mean, stdDev)) {
				errors.append(String.format(ERROR_MESSAGE_TEMPLATE, "10x", analytic.name(),
						analytic.level(),
						"Ten consecutive measurements on same side of mean, exceeding ±1 SD",
						"Systematic Error. Review calibration, reagent stability, and instrument maintenance. Recalibrate if necessary. "
								+ "If problem persists, consider retesting patient samples."));

				reportedViolations.add(violationKey);
			}
		}

		errors.append("</div>");
		return errors.toString();
	}

	private boolean oneThreeSigmaRule(List<Double> values, double mean, double stdDev) {
		for (double value : values) {
			if (value > mean + RULE_3S_MULTIPLIER * stdDev
					|| value < mean - RULE_3S_MULTIPLIER * stdDev) {
				return true;
			}
		}
		return false;
	}

	private boolean fourOneSigmaRule(List<Double> values, double mean, double stdDev) {
		if (values.size() < RULE_4_1S_CONSECUTIVE) {
			return false;
		}
		int countAbove = 0;
		int countBelow = 0;
		for (int i = 0; i <= 4; i++) {
			if (values.get(i) > mean + stdDev) {
				countAbove++;
				countBelow = 0;
			} else if (values.get(i) < mean - stdDev) {
				countBelow++;
				countAbove = 0;
			} else {
				countAbove = 0;
				countBelow = 0;
			}
			if (countAbove >= RULE_4_1S_CONSECUTIVE || countBelow >= RULE_4_1S_CONSECUTIVE) {
				return true;
			}
		}
		return false;
	}

	private boolean tenConsecutiveRule(List<Double> values, double mean, double stdDev) {
		if (values.size() < RULE_10X_CONSECUTIVE) {
			return false;
		}
		int countAbove = 0;
		int countBelow = 0;
		for (double value : values) {
			if (value > mean + stdDev) {
				countAbove++;
				countBelow = 0;
			} else if (value < mean - stdDev) {
				countBelow++;
				countAbove = 0;
			} else {
				countAbove = 0;
				countBelow = 0;
			}
			if (countAbove >= RULE_10X_CONSECUTIVE || countBelow >= RULE_10X_CONSECUTIVE) {
				return true;
			}
		}
		return false;
	}
}
