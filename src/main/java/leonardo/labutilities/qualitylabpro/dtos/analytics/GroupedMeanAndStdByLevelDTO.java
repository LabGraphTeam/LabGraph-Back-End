package leonardo.labutilities.qualitylabpro.dtos.analytics;

import java.util.List;

public record GroupedMeanAndStdByLevelDTO(String level, List<MeanAndStdDeviationDTO> values) {
}
