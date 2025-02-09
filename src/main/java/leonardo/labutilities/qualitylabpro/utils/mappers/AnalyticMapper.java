package leonardo.labutilities.qualitylabpro.utils.mappers;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.entities.Analytic;
import leonardo.labutilities.qualitylabpro.utils.components.RulesValidatorComponent;

public class AnalyticMapper {

	private AnalyticMapper() {

	}

	private static RulesValidatorComponent rulesValidatorComponent = new RulesValidatorComponent();

	// public static Analytic toEntity(AnalyticsDTO analyticsDTO,
	// RulesValidatorComponent rulesValidatorComponent) {
	// Analytic analytic = new Analytic();
	// analytic.setId(analyticsDTO.id());
	// analytic.setDate(analyticsDTO.date());
	// analytic.setLevelLot(analyticsDTO.level_lot());
	// analytic.setTestLot(analyticsDTO.test_lot());
	// analytic.setName(analyticsDTO.name());
	// analytic.setLevel(analyticsDTO.level());
	// analytic.setValue(analyticsDTO.value());
	// analytic.setMean(analyticsDTO.mean());
	// analytic.setSd(analyticsDTO.sd());
	// analytic.setUnitValue(analyticsDTO.unit_value());
	// rulesValidatorComponent.validator(analyticsDTO.value(), analyticsDTO.mean(),
	// analyticsDTO.sd());
	// analytic.setRules(rulesValidatorComponent.getRules());
	// analytic.setDescription(rulesValidatorComponent.getDescription());

	// return analytic;
	// }

	private static Analytic toEntityRulesValidator(AnalyticsDTO analyticsDTO) {
		Analytic analytic = new Analytic();
		analytic.setId(analyticsDTO.id());
		analytic.setDate(analyticsDTO.date());
		analytic.setLevelLot(analyticsDTO.level_lot());
		analytic.setTestLot(analyticsDTO.test_lot());
		analytic.setName(analyticsDTO.name());
		analytic.setLevel(analyticsDTO.level());
		analytic.setValue(analyticsDTO.value());
		analytic.setMean(analyticsDTO.mean());
		analytic.setSd(analyticsDTO.sd());
		analytic.setUnitValue(analyticsDTO.unit_value());
		rulesValidatorComponent.validator(analyticsDTO.value(), analyticsDTO.mean(),
				analyticsDTO.sd());
		analytic.setRules(rulesValidatorComponent.getRules());
		analytic.setDescription(rulesValidatorComponent.getDescription());

		return analytic;
	}

	public static Analytic toNewEntity(AnalyticsDTO analyticsDTO) {
		return toEntityRulesValidator(analyticsDTO);
	}

	public static Analytic toEntity(AnalyticsDTO analyticsDTO) {
		Analytic analytic = new Analytic();
		analytic.setId(analyticsDTO.id());
		analytic.setDate(analyticsDTO.date());
		analytic.setLevelLot(analyticsDTO.level_lot());
		analytic.setTestLot(analyticsDTO.test_lot());
		analytic.setName(analyticsDTO.name());
		analytic.setLevel(analyticsDTO.level());
		analytic.setValue(analyticsDTO.value());
		analytic.setMean(analyticsDTO.mean());
		analytic.setSd(analyticsDTO.sd());
		analytic.setUnitValue(analyticsDTO.unit_value());
		analytic.setRules(analyticsDTO.rules());
		analytic.setDescription(analyticsDTO.description());

		return analytic;
	}

	public static AnalyticsDTO toRecord(Analytic analytic) {
		return new AnalyticsDTO(analytic.getId(), analytic.getDate(), analytic.getLevelLot(),
				analytic.getTestLot(), analytic.getName(), analytic.getLevel(), analytic.getValue(),
				analytic.getMean(), analytic.getSd(), analytic.getUnitValue(), analytic.getRules(),
				analytic.getDescription());
	}
}
