package leonardo.labutilities.qualitylabpro.utils.mappers;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;
import leonardo.labutilities.qualitylabpro.utils.components.RulesValidatorComponent;

public class AnalyticsMapper {
    public static final RulesValidatorComponent rulesValidatorComponent = new RulesValidatorComponent();

    public static leonardo.labutilities.qualitylabpro.entities.Analytics toEntity(AnalyticsRecord record) {
        leonardo.labutilities.qualitylabpro.entities.Analytics analytics =
                new leonardo.labutilities.qualitylabpro.entities.Analytics();
        analytics.setDate(record.date());
        analytics.setLevelLot(record.level_lot());
        analytics.setTestLot(record.test_lot());
        analytics.setName(record.name());
        analytics.setLevel(record.level());
        analytics.setValue(record.value());
        analytics.setMean(record.mean());
        analytics.setSd(record.sd());
        analytics.setUnitValue(record.unit_value());
        rulesValidatorComponent.validator(record.value(), record.mean(), record.sd());
        analytics.setRules(rulesValidatorComponent.getRules());
        analytics.setDescription(rulesValidatorComponent.getDescription());

        return analytics;
    }

    public static AnalyticsRecord toRecord(leonardo.labutilities.qualitylabpro.entities.Analytics analytics) {
        return new AnalyticsRecord(
                analytics.getId(),
                analytics.getDate(),
                analytics.getLevelLot(),
                analytics.getTestLot(),
                analytics.getName(),
                analytics.getLevel(),
                analytics.getValue(),
                analytics.getMean(),
                analytics.getSd(),
                analytics.getUnitValue(),
                analytics.getRules(),
                analytics.getDescription()
        );
    }
}
