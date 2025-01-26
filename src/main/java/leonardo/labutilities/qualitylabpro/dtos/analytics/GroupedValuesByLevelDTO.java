package leonardo.labutilities.qualitylabpro.dtos.analytics;

import java.util.List;

public record GroupedValuesByLevelDTO(String level, List<AnalyticsDTO> values) {
}
