package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import leonardo.labutilities.qualitylabpro.domains.analytics.components.StatisticsCalculatorComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ComparativeErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling.ResourceNotFoundException;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;

@Service
public class AnalyticStatisticsService implements IAnalyticStatisticsService {

        private final AnalyticsRepository analyticsRepository;
        private final IAnalyticValidationService analyticsValidationService;

        public AnalyticStatisticsService(AnalyticsRepository analyticsRepository,
                        AnalyticValidationService analyticsValidationService) {
                this.analyticsRepository = analyticsRepository;
                this.analyticsValidationService = analyticsValidationService;

        }

        @Override
        public List<GroupedMeanAndStdByLevelDTO> returnMeanAndStandardDeviationForGroups(
                        List<GroupedValuesByLevelDTO> records) {
                return records.stream().map(group -> new GroupedMeanAndStdByLevelDTO(group.level(),
                                Collections.singletonList(StatisticsCalculatorComponent.computeStatistics(
                                                StatisticsCalculatorComponent.extractRecordValues(group.values())))))
                                .toList();
        }

        @Override
        public MeanAndStdDeviationDTO calculateMeanAndStandardDeviation(final String name, String level,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                List<AnalyticsDTO> values = analyticsRepository
                                .findByNameAndLevelAndDateBetween(name, level, dateStart, dateEnd, pageable).stream()
                                .map(AnalyticMapper::toRecord).filter(analyticsValidationService::isNotThreeSigma)
                                .toList();
                return StatisticsCalculatorComponent.calculateMeanAndStandardDeviation(values);
        }

        @Override
        public List<GroupedMeanAndStdByLevelDTO> calculateGroupedMeanAndStandardDeviation(final String name,
                        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
                List<AnalyticsDTO> records = analyticsRepository
                                .findByNameAndDateBetweenGroupByLevel(name, startDate, endDate, pageable).stream()
                                .map(AnalyticMapper::toRecord).toList();

                var values = records.stream().collect(Collectors.groupingBy(AnalyticsDTO::level)).entrySet().stream()
                                .map(entry -> new GroupedValuesByLevelDTO(entry.getKey(), entry.getValue())).toList();

                return returnMeanAndStandardDeviationForGroups(values);
        }

        @Override
        public List<ErrorStatisticsDTO> calculateErrorStatistics(final List<String> names, String level,
                        LocalDateTime startDate, LocalDateTime endDate) {
                var analytics = analyticsRepository.findByNameInAndLevelAndDateBetween(names, level, startDate, endDate,
                                Pageable.unpaged()).stream().toList();

                if (analytics.isEmpty()) {
                        throw new ResourceNotFoundException("No data found for the given parameters");
                }

                Map<String, List<AnalyticsDTO>> analyticsByName =
                                analytics.stream().collect(Collectors.groupingBy(AnalyticsDTO::name));

                List<ErrorStatisticsDTO> result = new ArrayList<>();
                for (Map.Entry<String, List<AnalyticsDTO>> entry : analyticsByName.entrySet()) {
                        List<AnalyticsDTO> analyticsForName = entry.getValue();

                        String defalutName = analyticsForName.getFirst().name();

                        String defaultLevel = analyticsForName.getFirst().level();

                        double defaultMean = analyticsForName.getFirst().mean();

                        ErrorStatisticsDTO stat = StatisticsCalculatorComponent.calculateErrorStatistics(
                                        analyticsForName, defalutName, defaultLevel, defaultMean);
                        result.add(stat);
                }

                return result;
        }

        @Override
        public ComparativeErrorStatisticsDTO calculateComparativeErrorStatistics(final String analyticName,
                        String level, LocalDateTime firstStartDate, LocalDateTime firstEndDate,
                        LocalDateTime secondStartDate, LocalDateTime secondEndDate) {

                List<String> monthList = List.of(firstStartDate.getMonth().name(), secondStartDate.getMonth().name());

                List<AnalyticsDTO> firstAnalytic =
                                analyticsRepository
                                                .findByNameAndLevelAndDateBetween(analyticName, level, firstStartDate,
                                                                firstEndDate, Pageable.unpaged())
                                                .stream().map(AnalyticMapper::toRecord).toList();

                List<AnalyticsDTO> secondAnalytic =
                                analyticsRepository
                                                .findByNameAndLevelAndDateBetween(analyticName, level, secondStartDate,
                                                                secondEndDate, Pageable.unpaged())
                                                .stream().map(AnalyticMapper::toRecord).toList();

                if (firstAnalytic.isEmpty() || secondAnalytic.isEmpty()) {
                        throw new ResourceNotFoundException("No data found for the given parameters");
                }

                double defaultMean = firstAnalytic.getFirst().mean();

                return StatisticsCalculatorComponent.calculateComparativeErrorStatistics(analyticName, level,
                                defaultMean, firstAnalytic, secondAnalytic, monthList);
        }
}
