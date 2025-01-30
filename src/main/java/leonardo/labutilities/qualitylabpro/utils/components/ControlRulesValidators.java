package leonardo.labutilities.qualitylabpro.utils.components;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.utils.blacklist.AnalyticsBlackList;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static leonardo.labutilities.qualitylabpro.utils.constants.EmailTemplate.ERROR_MESSAGE_TEMPLATE;

@Component
public class ControlRulesValidators {

	private final AnalyticsRepository analyticsRepository;

	public ControlRulesValidators(AnalyticsRepository analyticsRepository) {
		this.analyticsRepository = analyticsRepository;
	}




	public String validateRules(List<AnalyticsDTO> analytics) {
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

			var analyticsRecords = analyticsRepository.findLast10ByNameAndLevel(analytic.name(), analytic.level())
					.stream()
					.filter(record -> !AnalyticsBlackList.BLACK_LIST.contains(record.name()))
					.toList();
			var mean = analyticsRecords.getFirst().mean();
			var stdDev = analyticsRecords.getFirst().sd();
			var values = analyticsRecords.stream().map(AnalyticsDTO::value).toList();


			if (rule1_3s(values, mean, stdDev)) {
				errors.append(String.format(ERROR_MESSAGE_TEMPLATE, "1-3s", analytic.name(),
						analytic.level(), "One observation exceeds mean ±3 SD",
						"Random Error. Reject run and investigate for potential systematic errors."));
				reportedViolations.add(violationKey);
			}

			if (rule4_1s(values, mean, stdDev)) {
				errors.append(String.format(ERROR_MESSAGE_TEMPLATE, "4-1s", analytic.name(),
						analytic.level(),
						"Four consecutive measurements exceed ±1 SD on same side of mean",
						"Systematic Error. Check for calibration drift, reagent lot changes, or environmental conditions."));
				reportedViolations.add(violationKey);
			}

			if (rule10x(values, mean, stdDev)) {
				errors.append(String.format(ERROR_MESSAGE_TEMPLATE, "10x", analytic.name(),
						analytic.level(), "Ten consecutive measurements on same side of mean",
						"Systematic Error. Review calibration, reagent stability, and instrument maintenance. Recalibrate if necessary."));
				reportedViolations.add(violationKey);
			}
		}

		errors.append("</div>");
		return errors.toString();
	}

	public boolean rule1_3s(List<Double> values, double mean, double stdDev) {
		for (double value : values) {
			if (value > mean + 3 * stdDev || value < mean - 3 * stdDev) {
				return true;
			}
		}
		return false;
	}

	public boolean rule4_1s(List<Double> values, double mean, double stdDev) {
		if (values.size() < 4) {
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
			if (countAbove >= 4 || countBelow >= 4) {
				return true;
			}
		}
		return false;
	}

	public boolean rule10x(List<Double> values, double mean, double stdDev) {
		if (values.size() < 10) {
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
			if (countAbove >= 10 || countBelow >= 10) {
				return true;
			}
		}
		return false;
	}
}
