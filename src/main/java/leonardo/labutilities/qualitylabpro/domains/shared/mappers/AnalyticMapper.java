package leonardo.labutilities.qualitylabpro.domains.shared.mappers;

import leonardo.labutilities.qualitylabpro.domains.analytics.components.SpecsValidatorComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

public class AnalyticMapper {

	private AnalyticMapper() {

	}

	private static final SpecsValidatorComponent rulesValidatorComponent = new SpecsValidatorComponent();

	private static Analytic toEntityRulesValidator(AnalyticsDTO analyticsDTO) {
		Analytic analytic = new Analytic();
		analytic.setId(analyticsDTO.id());
		analytic.setMeasurementDate(analyticsDTO.date());
		analytic.setControlLevelLot(analyticsDTO.level_lot());
		analytic.setReagentLot(analyticsDTO.test_lot());
		analytic.setTestName(analyticsDTO.name());
		analytic.setControlLevel(analyticsDTO.level());
		analytic.setMeasurementValue(analyticsDTO.value());
		analytic.setTargetMean(analyticsDTO.mean());
		analytic.setStandardDeviation(analyticsDTO.sd());
		analytic.setMeasurementUnit(analyticsDTO.unit_value());
		rulesValidatorComponent.validator(analyticsDTO.value(), analyticsDTO.mean(),
				analyticsDTO.sd());
		analytic.setControlRules(rulesValidatorComponent.getRules());
		analytic.setDescription(rulesValidatorComponent.getDescription());

		return analytic;
	}

	public static Analytic toNewEntity(AnalyticsDTO analyticsDTO) {
		return toEntityRulesValidator(analyticsDTO);
	}

	public static Analytic toEntity(AnalyticsDTO analyticsDTO) {
		Analytic analytic = new Analytic();
		analytic.setId(analyticsDTO.id());
		analytic.setMeasurementDate(analyticsDTO.date());
		analytic.setControlLevelLot(analyticsDTO.level_lot());
		analytic.setReagentLot(analyticsDTO.test_lot());
		analytic.setTestName(analyticsDTO.name());
		analytic.setControlLevel(analyticsDTO.level());
		analytic.setMeasurementValue(analyticsDTO.value());
		analytic.setTargetMean(analyticsDTO.mean());
		analytic.setStandardDeviation(analyticsDTO.sd());
		analytic.setMeasurementUnit(analyticsDTO.unit_value());
		analytic.setControlRules(analyticsDTO.rules());
		analytic.setDescription(analyticsDTO.description());
		analytic.setValidatorUserId(new User());
		analytic.setOwnerUserId(new User());

		return analytic;
	}

	public static AnalyticsDTO toRecord(Analytic analytic) {
		return new AnalyticsDTO(analytic.getId(), analytic.getMeasurementDate(),
				analytic.getControlLevelLot(), analytic.getReagentLot(), analytic.getTestName(),
				analytic.getControlLevel(), analytic.getMeasurementValue(),
				analytic.getTargetMean(), analytic.getStandardDeviation(),
				analytic.getMeasurementUnit(), analytic.getControlRules(),
				analytic.getDescription(), getValidatorUsername(analytic),
				getOwnerUsername(analytic));
	}

	private static String getValidatorUsername(Analytic analytic) {
		try {
			return analytic.getValidatorUserId() != null ? analytic.getValidatorUserId().getUsername()
					: "Not validated";
		} catch (Exception e) {
			throw new RuntimeException("Error getting validator username", e);
		}
	}

	private static String getOwnerUsername(Analytic analytic) {
		try {
			return analytic.getOwnerUserId() != null ? analytic.getOwnerUserId().getUsername()
					: "Not validated";
		} catch (Exception e) {
			throw new RuntimeException("Error getting validator username", e);
		}
	}
}
