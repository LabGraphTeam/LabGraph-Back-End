package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;

public interface IAnalyticsStatisticsService {
        MeanAndStdDeviationDTO calculateMeanAndStandardDeviation(String name, String level,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

        List<GroupedMeanAndStdByLevelDTO> calculateGroupedMeanAndStandardDeviation(String name,
                        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

        List<GroupedMeanAndStdByLevelDTO> returnMeanAndStandardDeviationForGroups(
                        List<GroupedValuesByLevelDTO> records);
}
