package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;

public interface IAnalyticsValidationService {
    boolean isGroupedRecordValid(GroupedValuesByLevelDTO groupedValuesByLevelDTO);

    boolean isNotThreeSigma(AnalyticsDTO analyticsDTO);

    boolean isNewAnalyticRecord(AnalyticsDTO values);

    void ensureAnalyticTestNameExists(String name);

}
