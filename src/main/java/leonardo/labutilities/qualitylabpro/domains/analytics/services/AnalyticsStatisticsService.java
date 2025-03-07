package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.StatisticsCalcComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;

@Service
public class AnalyticsStatisticsService implements IAnalyticsStatisticsService {

        private final AnalyticsRepository analyticsRepository;
        private final IAnalyticsValidationService analyticsValidationService;

        public AnalyticsStatisticsService(AnalyticsRepository analyticsRepository,
                        AnalyticsValidationService analyticsValidationService) {
                this.analyticsRepository = analyticsRepository;
                this.analyticsValidationService = analyticsValidationService;

        }

        @Override
        public List<GroupedMeanAndStdByLevelDTO> returnMeanAndStandardDeviationForGroups(
                        List<GroupedValuesByLevelDTO> records) {
                return records.stream().map(group -> new GroupedMeanAndStdByLevelDTO(group.level(),
                                Collections.singletonList(StatisticsCalcComponent.computeStatistics(
                                                StatisticsCalcComponent.extractRecordValues(
                                                                group.values())))))
                                .toList();
        }

        @Override
        @Cacheable(value = "meanAndStdDeviation",
                        key = "{#name, #level, #dateStart, #dateEnd, #pageable.pageNumber, #pageable.pageSize}")
        public MeanAndStdDeviationDTO calculateMeanAndStandardDeviation(String name, String level,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                List<AnalyticsDTO> values = analyticsRepository
                                .findByNameAndLevelAndDateBetween(name, level, dateStart, dateEnd,
                                                pageable)
                                .stream().map(AnalyticMapper::toRecord)
                                .filter(analyticsValidationService::isNotThreeSigma).toList();
                return StatisticsCalcComponent.calcMeanAndStandardDeviationOptimized(values);
        }

        @Override
        @Cacheable(value = "calculateGroupedMeanAndStandardDeviation",
                        key = "{#name, #level, #dateStart, #dateEnd, #pageable.pageNumber, #pageable.pageSize}")
        public List<GroupedMeanAndStdByLevelDTO> calculateGroupedMeanAndStandardDeviation(
                        String name, LocalDateTime startDate, LocalDateTime endDate,
                        Pageable pageable) {
                List<AnalyticsDTO> records = analyticsRepository
                                .findByNameAndDateBetweenGroupByLevel(name, startDate, endDate,
                                                pageable)
                                .stream().map(AnalyticMapper::toRecord).toList();
                var values = records.stream().collect(Collectors.groupingBy(AnalyticsDTO::level))
                                .entrySet().stream()
                                .map(entry -> new GroupedValuesByLevelDTO(entry.getKey(),
                                                entry.getValue()))
                                .toList();

                return returnMeanAndStandardDeviationForGroups(values);
        }

}
