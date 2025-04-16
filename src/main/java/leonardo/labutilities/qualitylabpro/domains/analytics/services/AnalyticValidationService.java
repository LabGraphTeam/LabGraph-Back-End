package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.util.Objects;

import org.springframework.stereotype.Service;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@Service
public class AnalyticValidationService implements IAnalyticValidationService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticValidationService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @Override
    public final boolean isGroupedRecordValid(GroupedValuesByLevelDTO groupedValuesByLevelDTO) {
        return groupedValuesByLevelDTO.values().stream()
                .allMatch(groupedValue -> !Objects.equals(groupedValue.rules(), "+3s")
                        && !Objects.equals(groupedValue.rules(), "-3s"));
    }

    @Override
    public final boolean isNotThreeSigma(AnalyticsDTO analyticsDTO) {
        String rules = analyticsDTO.rules();
        return (!Objects.equals(rules, "+3s") || !Objects.equals(rules, "-3s"));
    }

    @Override
    public final boolean isNewAnalyticRecord(AnalyticsDTO values) {
        return !this.analyticsRepository.existsByMeasurementDateAndControlLevelAndTestName(values.date(),
                values.level(), values.name());
    }

    @Override
    public final void ensureAnalyticTestNameExists(String name) {
        if (!this.analyticsRepository.existsByTestName(name.toUpperCase())) {
            throw new CustomGlobalErrorHandling.ResourceNotFoundException("Analytics by name not available");
        }
    }

}
