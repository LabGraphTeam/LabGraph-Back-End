package leonardo.labutilities.qualitylabpro.utils.mappers;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.entities.Analytic;
import leonardo.labutilities.qualitylabpro.utils.components.RulesValidatorComponent;

public class AnalyticMapper {
    public static final RulesValidatorComponent rulesValidatorComponent = new RulesValidatorComponent();

    public static Analytic toEntity(AnalyticsDTO record) {
        Analytic analytic =
                new Analytic();
        analytic.setDate(record.date());
        analytic.setLevelLot(record.level_lot());
        analytic.setTestLot(record.test_lot());
        analytic.setName(record.name());
        analytic.setLevel(record.level());
        analytic.setValue(record.value());
        analytic.setMean(record.mean());
        analytic.setSd(record.sd());
        analytic.setUnitValue(record.unit_value());
        rulesValidatorComponent.validator(record.value(), record.mean(), record.sd());
        analytic.setRules(rulesValidatorComponent.getRules());
        analytic.setDescription(rulesValidatorComponent.getDescription());

        return analytic;
    }

    public static AnalyticsDTO toRecord(Analytic analytic) {
        return new AnalyticsDTO(
                analytic.getId(),
                analytic.getDate(),
                analytic.getLevelLot(),
                analytic.getTestLot(),
                analytic.getName(),
                analytic.getLevel(),
                analytic.getValue(),
                analytic.getMean(),
                analytic.getSd(),
                analytic.getUnitValue(),
                analytic.getRules(),
                analytic.getDescription()
        );
    }
}
