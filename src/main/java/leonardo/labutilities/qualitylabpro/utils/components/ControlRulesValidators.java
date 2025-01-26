package leonardo.labutilities.qualitylabpro.utils.components;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ControlRulesValidators {

	private final AnalyticsRepository analyticsRepository;

	public ControlRulesValidators(AnalyticsRepository analyticsRepository) {
		this.analyticsRepository = analyticsRepository;
	}

	private static final String ERROR_MESSAGE_TEMPLATE =
			"""
					<div style="margin: 10px 0; padding: 15px; border-left: 4px solid #ff4444; background-color: #fff3f3;">
					    <h3 style="color: #cc0000; margin: 0 0 10px 0;">%s Rule Violation</h3>
					    <p style="margin: 0; color: #333;">
					        <strong>Test:</strong> %s<br>
					        <strong>Level:</strong> %s<br>
					        <strong>Issue:</strong> %s<br>
					        <strong>Action Required:</strong> %s
					    </p>
					</div>
					""";


	public String validateRules(List<AnalyticsDTO> records) {
		StringBuilder errors = new StringBuilder();
		errors.append("<div style='font-family: Arial, sans-serif;'>");

		// Track already reported violations
		Set<String> reportedViolations = new HashSet<>();

		for (AnalyticsDTO record : records) {
			// Create unique key for test/level combination
			String violationKey = record.name() + "-" + record.level();

			// Skip if we already reported this violation
			if (reportedViolations.contains(violationKey)) {
				continue;
			}

			var analyticsRecords =
					analyticsRepository.findLast10ByNameAndLevel(record.name(), record.level());
			var mean = analyticsRecords.getFirst().mean();
			var stdDev = analyticsRecords.getFirst().sd();
			var values = analyticsRecords.stream().map(AnalyticsDTO::value).toList();

			if (rule4_1s(values, mean, stdDev)) {
				errors.append(String.format(ERROR_MESSAGE_TEMPLATE, "4-1s", record.name(),
						record.level(),
						"Four consecutive observations exceed 1 SD on the same side of the mean.",
						"Investigate possible trends or deviations in the process."));
				reportedViolations.add(violationKey);
			}

			if (rule10x(values, mean, stdDev)) {
				errors.append(String.format(ERROR_MESSAGE_TEMPLATE, "10x", record.name(),
						record.level(), "Ten consecutive results are on the same side of the mean.",
						"Perform maintenance or calibration on the equipment."));
				reportedViolations.add(violationKey);
			}
		}

		errors.append("</div>");
		return errors.toString();
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
