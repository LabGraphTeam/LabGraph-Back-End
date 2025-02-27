package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.RulesProviderComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.blacklist.AnalyticsBlackList;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnalyticHelperService implements IAnalyticHelperService {

        private final AnalyticsRepository analyticsRepository;
        private final EmailService emailService;
        private final RulesProviderComponent controlRulesValidators;

        public AnalyticHelperService(AnalyticsRepository analyticsRepository,
                        EmailService emailService, RulesProviderComponent controlRulesValidators) {
                this.analyticsRepository = analyticsRepository;
                this.emailService = emailService;
                this.controlRulesValidators = controlRulesValidators;
        }

        public String convertLevel(String level) {
                return level.toUpperCase();
        }

        // VALIDATION METHODS
        private static void validateResultsNotEmpty(List<?> results, String message) {
                if (results == null || results.isEmpty()) {
                        throw new CustomGlobalErrorHandling.ResourceNotFoundException(message);
                }
        }

        public boolean isAnalyticsNonExistent(AnalyticsDTO values) {
                return !this.analyticsRepository.existsByMeasurementDateAndControlLevelAndTestName(
                                values.date(), values.level(), values.name());
        }

        private static boolean isRuleBroken(Analytic analytic) {
                String rules = analytic.getControlRules();
                return ("+3s".equals(rules) || "-3s".equals(rules) || "-2s".equals(rules)
                                || "+2s".equals(rules));
        }

        public void ensureNameExists(String name) {
                if (!this.analyticsRepository.existsByTestName(name.toUpperCase())) {
                        throw new CustomGlobalErrorHandling.ResourceNotFoundException(
                                        "AnalyticsDTO by name not found");
                }
        }

        private static List<Analytic> filterFailedRecords(List<Analytic> persistedRecords) {
                return persistedRecords.stream().filter(AnalyticHelperService::isRuleBroken)
                                .filter(analytics -> !AnalyticsBlackList.BLACK_LIST
                                                .contains(analytics.getTestName()))
                                .toList();
        }

        @Async
        public void processFailedRecordsNotification(List<AnalyticsDTO> failedRecords) {
                if (!failedRecords.isEmpty()) {
                        try {
                                var content = this.controlRulesValidators
                                                .validateRules(failedRecords);
                                this.emailService.sendFailedAnalyticsNotification(failedRecords,
                                                content);
                        } catch (Exception e) {
                                log.error("Error sending identifier notification: {}", e);
                        }
                }
        }

        // STATISTICS METHODS
        private static MeanAndStdDeviationDTO computeStatistics(List<Double> values) {
                double sum = values.stream().mapToDouble(Double::doubleValue).sum();
                int size = values.size();
                double mean = sum / size;
                double variance = values.stream().mapToDouble(value -> Math.pow(value - mean, 2))
                                .average().orElse(0.0);
                return new MeanAndStdDeviationDTO(mean, Math.sqrt(variance));
        }

        private static List<Double> extractRecordValues(List<AnalyticsDTO> records) {
                return records.stream().map(AnalyticsDTO::value).toList();
        }

        @Override
        public List<GroupedMeanAndStdByLevelDTO> returnMeanAndStandardDeviationForGroups(
                        List<GroupedValuesByLevelDTO> records) {
                return records.stream().map(group -> new GroupedMeanAndStdByLevelDTO(group.level(),
                                Collections.singletonList(computeStatistics(
                                                extractRecordValues(group.values())))))
                                .toList();
        }

        @Cacheable(value = "meanAndStdDeviation",
                        key = "{#name, #level, #dateStart, #dateEnd, #pageable.pageNumber, #pageable.pageSize}")
        public MeanAndStdDeviationDTO calculateMeanAndStandardDeviation(String name, String level,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                List<AnalyticsDTO> values = this
                                .findAnalyticsByNameAndLevelAndDate(name, level, dateStart, dateEnd,
                                                pageable)
                                .stream().filter(this::isRecordValid).toList();
                return computeStatistics(extractRecordValues(values));
        }

        public MeanAndStdDeviationDTO calcMeanAndStandardDeviationOptimized(
                        List<AnalyticsDTO> values) {
                return computeStatistics(extractRecordValues(values));
        }

        @Cacheable(value = "calculateGroupedMeanAndStandardDeviation",
                        key = "{#name, #level, #dateStart, #dateEnd, #pageable.pageNumber, #pageable.pageSize}")
        public List<GroupedMeanAndStdByLevelDTO> calculateGroupedMeanAndStandardDeviation(
                        String name, LocalDateTime startDate, LocalDateTime endDate,
                        Pageable pageable) {
                List<AnalyticsDTO> records = this.analyticsRepository
                                .findByNameAndDateBetweenGroupByLevel(name, startDate, endDate,
                                                pageable)
                                .stream().map(AnalyticMapper::toRecord).toList();
                var values = records.stream().collect(Collectors.groupingBy(AnalyticsDTO::level))
                                .entrySet().stream()
                                .map(entry -> new GroupedValuesByLevelDTO(entry.getKey(),
                                                entry.getValue()))
                                .toList();

                return this.returnMeanAndStandardDeviationForGroups(values);
        }

        // BUSINESS LOGIC METHODS
        public List<GroupedResultsByLevelDTO> findAnalyticsWithGroupedResults(String name,
                        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
                List<GroupedValuesByLevelDTO> analytics = this.findGroupedAnalyticsByLevel(name,
                                startDate, endDate, pageable);
                Map<String, MeanAndStdDeviationDTO> statsByLevel = analytics.stream()
                                .collect(Collectors.toMap(GroupedValuesByLevelDTO::level,
                                                group -> computeStatistics(extractRecordValues(
                                                                group.values()))));

                return analytics.stream().map(analytic -> new GroupedResultsByLevelDTO(analytic,
                                new GroupedMeanAndStdByLevelDTO(analytic.level(),
                                                Collections.singletonList(statsByLevel
                                                                .get(analytic.level())))))
                                .toList();
        }

        @Override
        public List<GroupedValuesByLevelDTO> findGroupedAnalyticsByLevel(String name,
                        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
                List<AnalyticsDTO> records = this.analyticsRepository
                                .findByNameAndDateBetweenGroupByLevel(name, startDate, endDate,
                                                pageable)
                                .stream().map(AnalyticMapper::toRecord).toList();
                validateResultsNotEmpty(records,
                                "No analytics found for the given name and date between parameters");

                return records.stream().collect(Collectors.groupingBy(AnalyticsDTO::level))
                                .entrySet().stream()
                                .map(entry -> new GroupedValuesByLevelDTO(entry.getKey(),
                                                entry.getValue()))
                                .toList();
        }

        @Override
        public Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetweenWithLinks(List<String> names,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                return this.analyticsRepository.findByNameInAndDateBetweenPaged(names, dateStart,
                                dateEnd, pageable);
        }

        @Override
        public void updateAnalyticsMeanByNameAndLevelAndLevelLot(String name, String level,
                        String levelLot, double mean) {
                this.analyticsRepository.updateMeanByNameAndLevelAndLevelLot(name, level, levelLot,
                                mean);
        }

        @Override
        public boolean isGroupedRecordValid(GroupedValuesByLevelDTO groupedValuesByLevelDTO) {
                return groupedValuesByLevelDTO.values().stream().allMatch(
                                groupedValue -> !Objects.equals(groupedValue.rules(), "+3s")
                                                && !Objects.equals(groupedValue.rules(), "-3s"));
        }

        @Override
        public boolean isRecordValid(AnalyticsDTO analyticsDTO) {
                String rules = analyticsDTO.rules();
                return (!Objects.equals(rules, "+3s") || !Objects.equals(rules, "-3s"));
        }

        @Override
        public AnalyticsDTO findOneById(Long id) {
                return AnalyticMapper.toRecord(this.analyticsRepository.findById(id).orElseThrow(
                                () -> new CustomGlobalErrorHandling.ResourceNotFoundException(
                                                "AnalyticsDTO by id not found")));
        }

        @Override
        @CacheEvict(value = { "analyticsByNameAndDateRange", "meanAndStdDeviation",
                        "calculateGroupedMeanAndStandardDeviation",
                        "AnalyticsByNameWithPagination" }, allEntries = true)
        public void saveNewAnalyticsRecords(List<AnalyticsDTO> valuesOfLevelsList) {

                var newRecords = valuesOfLevelsList.stream().filter(this::isAnalyticsNonExistent)
                                .map(AnalyticMapper::toNewEntity).toList();

                if (newRecords.isEmpty()) {
                        log.warn("No new analytics records to save.");
                        throw new CustomGlobalErrorHandling.DataIntegrityViolationException();
                }

                List<Analytic> persistedRecords = this.analyticsRepository.saveAll(newRecords);

                List<AnalyticsDTO> failedRecords = filterFailedRecords(persistedRecords).stream()
                                .map(AnalyticMapper::toRecord).toList();

                processFailedRecordsNotification(failedRecords);
        }

        @Cacheable("AnalyticsByNameWithPagination")
        @Override
        public List<AnalyticsDTO> findAnalyticsByNameWithPagination(List<String> names, String name,
                        Pageable pageable) {

                if (names.contains(name)) {
                        List<AnalyticsDTO> analyticsList = this.analyticsRepository
                                        .findByTestName(name.toUpperCase(), pageable).stream()
                                        .map(AnalyticMapper::toRecord).toList();
                        validateResultsNotEmpty(analyticsList,
                                        "No analytics found with the given name");
                        return analyticsList;
                }
                throw new CustomGlobalErrorHandling.ResourceNotFoundException(
                                "AnalyticsDTO by name not found");
        }

        @Override
        public Page<AnalyticsDTO> findAnalyticsByNameInByLevelBaseMethod(List<String> names,
                        String level, LocalDateTime startDate, LocalDateTime endDate,
                        Pageable pageable) {
                Page<AnalyticsDTO> results =
                                this.analyticsRepository.findByNameInAndLevelAndDateBetween(names,
                                                level, startDate, endDate, pageable);
                validateResultsNotEmpty(results.getContent(),
                                "No analytics found for the given parameters with pagination");
                return results;
        }

        @Override
        public List<AnalyticsDTO> findAnalyticsByDate(LocalDateTime dateStart,
                        LocalDateTime dateEnd) {
                List<AnalyticsDTO> results =
                                this.analyticsRepository.findByDateBetween(dateStart, dateEnd)
                                                .stream().map(AnalyticMapper::toRecord).toList();
                validateResultsNotEmpty(results, "No analytics found for the given date range");
                return results;
        }

        @Override
        @Cacheable(value = "analyticsByNameAndDateRange",
                        key = "{#names.hashCode(), #dateStart, #dateEnd, #pageable.pageNumber, #pageable.pageSize}")
        public Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetween(List<String> names,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                return this.analyticsRepository.findByNameInAndDateBetweenPaged(names, dateStart,
                                dateEnd, pageable);
        }

        @Override
        public void deleteAnalyticsById(Long id) {
                if (!this.analyticsRepository.existsById(id)) {
                        throw new CustomGlobalErrorHandling.ResourceNotFoundException(
                                        "AnalyticsDTO by id not found");
                }
                this.analyticsRepository.deleteById(id);
        }

        public List<AnalyticsDTO> findAnalyticsByNameIn(List<String> names, Pageable pageable) {
                return this.analyticsRepository.findByNameIn(names, pageable).stream()
                                .map(AnalyticMapper::toRecord).toList();
        }

        public Page<AnalyticsDTO> findAnalyticsPagedByNameIn(List<String> names,
                        Pageable pageable) {
                return this.analyticsRepository.findByNameInPaged(names, pageable);
        }

        public List<AnalyticsDTO> findAnalyticsByNameAndLevelWithPagination(Pageable pageable,
                        String name, String level) {
                List<AnalyticsDTO> analyticsList = this.analyticsRepository
                                .findByNameAndLevel(pageable, name.toUpperCase(), level).stream()
                                .map(AnalyticMapper::toRecord).toList();
                validateResultsNotEmpty(analyticsList,
                                "No analytics found for the given name and level");
                return analyticsList;
        }

        public List<AnalyticsDTO> findAnalyticsByNameLevelAndDate(String name, String level,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                List<AnalyticsDTO> results = this.analyticsRepository
                                .findByNameAndLevelAndDateBetween(name, level, dateStart, dateEnd,
                                                pageable)
                                .stream().map(AnalyticMapper::toRecord).toList();
                validateResultsNotEmpty(results,
                                "No analytics found for the given name, level, dateStart, dateEnd -> parameters");
                return results;
        }

        public AnalyticsWithCalcDTO findAnalyticsByNameLevelDateOptimized(String name, String level,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                List<AnalyticsDTO> results = this.analyticsRepository
                                .findByNameAndLevelAndDateBetween(name, level, dateStart, dateEnd,
                                                pageable)
                                .stream().map(AnalyticMapper::toRecord).toList();

                var calcSdAndMean = this.calcMeanAndStandardDeviationOptimized(results);

                AnalyticsWithCalcDTO analyticsWithCalcDTO =
                                new AnalyticsWithCalcDTO(results, calcSdAndMean);

                validateResultsNotEmpty(results,
                                "No analytics found for the given name, level, dateStart, dateEnd -> parameters");
                return analyticsWithCalcDTO;
        }

        public Page<AnalyticsDTO> findAnalyticsByNameInByLevel(List<String> names, String level,
                        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
                return this.findAnalyticsByNameInByLevelBaseMethod(names, this.convertLevel(level),
                                startDate, endDate, pageable);
        }

        @Override
        public List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable, String name,
                        String level) {
                this.ensureNameExists(name);
                return this.findAnalyticsByNameAndLevelWithPagination(pageable, name,
                                this.convertLevel(level));
        }

        @Override
        public List<AnalyticsDTO> findAnalyticsByNameAndLevelAndDate(String name, String level,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                return this.findAnalyticsByNameLevelAndDate(name, this.convertLevel(level),
                                dateStart, dateEnd, pageable);
        }

        @Override
        public AnalyticsWithCalcDTO findAnalyticsByNameLevelDate(String name, String level,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                return this.findAnalyticsByNameLevelDateOptimized(name, this.convertLevel(level),
                                dateStart, dateEnd, pageable);
        }

}
