package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import java.util.List;
import org.springframework.stereotype.Component;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.ThresholdAnalyticsRules;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.shared.blacklist.AnalyticsBlackList;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@Component
public class AnalyticObjectValidationComponent {
    private AnalyticObjectValidationComponent() {}

    public static void validateResultsNotEmpty(List<?> results, String message) {
        if (results == null || results.isEmpty()) {
            throw new CustomGlobalErrorHandling.ResourceNotFoundException(message);
        }
    }

    public static boolean isRuleBroken(Analytic analytic) {
        String rules = analytic.getControlRules();
        return (ThresholdAnalyticsRules.RULES.contains(rules));
    }

    public static List<Analytic> filterFailedRecords(List<Analytic> persistedRecords) {
        return persistedRecords.stream().filter(AnalyticObjectValidationComponent::isRuleBroken)
                .filter(analytics -> !AnalyticsBlackList.BLACK_LIST
                        .contains(analytics.getTestName()))
                .toList();
    }

}
