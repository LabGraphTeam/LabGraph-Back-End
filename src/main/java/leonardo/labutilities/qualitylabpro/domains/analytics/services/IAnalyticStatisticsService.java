package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ComparativeErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;

public interface IAnalyticStatisticsService {
        MeanAndStdDeviationDTO calculateMeanAndStandardDeviation(String testName, String level,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

        List<GroupedMeanAndStdByLevelDTO> calculateGroupedMeanAndStandardDeviation(String testName,
                        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

        List<GroupedMeanAndStdByLevelDTO> returnMeanAndStandardDeviationForGroups(
                        List<GroupedValuesByLevelDTO> records);

        List<ErrorStatisticsDTO> calculateErrorStatistics(List<String> names, String level,
                        LocalDateTime startDate, LocalDateTime endDate);

        ComparativeErrorStatisticsDTO calculateComparativeErrorStatistics(String analyticName,
                        String level, LocalDateTime fisttStartDate, LocalDateTime firstEndDate,
                        LocalDateTime secondStartDate,
                        LocalDateTime secondEndDate);

}
