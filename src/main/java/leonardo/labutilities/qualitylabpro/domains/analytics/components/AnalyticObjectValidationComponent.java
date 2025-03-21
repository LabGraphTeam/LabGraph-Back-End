package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import leonardo.labutilities.qualitylabpro.domains.analytics.constants.ThresholdAnalyticsRules;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.shared.blacklist.AnalyticsBlackList;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@Component
public final class AnalyticObjectValidationComponent {
    private AnalyticObjectValidationComponent() {}

    public static void validateResultsNotEmpty(List<?> results, final String message) {
        System.out.println(results);

        if (results == null || results.isEmpty()) {
            throw new CustomGlobalErrorHandling.ResourceNotFoundException(message);
        }
    }

    public static boolean isRuleBroken(final Analytic analytic) {
        return Optional.ofNullable(analytic.getControlRules())
                .filter(rules -> !"+1s".equals(rules) && !"-1s".equals(rules))
                .map(ThresholdAnalyticsRules.RULES::contains)
                .orElse(false);
    }

    public static List<Analytic> filterFailedRecords(final List<Analytic> persistedRecords) {
        return Optional.ofNullable(persistedRecords)
                .map(records -> records.stream()
                        .filter(analytics -> isRuleBroken(analytics) &&
                                !AnalyticsBlackList.BLACK_LIST.toString()
                                        .equals(analytics.getTestName()))
                        .toList())
                .orElse(List.of());

    }
}
