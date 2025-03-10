package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import leonardo.labutilities.qualitylabpro.domains.analytics.components.AnalyticFailedNotificationComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.AnalyticObjectValidationComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.StatisticsCalculatorComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnalyticHelperService implements IAnalyticHelperService {

        private final AnalyticsRepository analyticsRepository;
        private final IAnalyticsValidationService analyticsValidationService;
        private final AnalyticFailedNotificationComponent analyticFailedNotificationComponent;

        public AnalyticHelperService(AnalyticsRepository analyticsRepository,
                        AnalyticFailedNotificationComponent analyticFailedNotificationComponent,
                        AnalyticsValidationService analyticsValidationService) {
                this.analyticsRepository = analyticsRepository;
                this.analyticFailedNotificationComponent = analyticFailedNotificationComponent;
                this.analyticsValidationService = analyticsValidationService;
        }

        // ==================== CORE CRUD OPERATIONS ====================

        @Override
        public AnalyticsDTO findOneById(Long id) {
                return AnalyticMapper.toRecord(analyticsRepository.findById(id)
                                .orElseThrow(() -> new CustomGlobalErrorHandling.ResourceNotFoundException(
                                                "AnalyticsDTO by id not found")));
        }

        @Override
        public void deleteAnalyticsById(Long id) {
                if (!analyticsRepository.existsById(id)) {
                        throw new CustomGlobalErrorHandling.ResourceNotFoundException(
                                        "AnalyticsDTO by id not found");
                }
                analyticsRepository.deleteById(id);
        }

        @Override
        @CacheEvict(value = {"analyticsByNameAndDateRange", "meanAndStdDeviation",
                        "calculateGroupedMeanAndStandardDeviation",
                        "AnalyticsByNameWithPagination"},
                        allEntries = true)

        // ==================== USER OPERATIONS ====================

        public void saveNewAnalyticsRecords(List<AnalyticsDTO> valuesOfLevelsList) {

                var newAnalyticsRecords = valuesOfLevelsList.stream()
                                .filter(analyticsValidationService::isNewAnalyticRecord)
                                .map(AnalyticMapper::toNewEntity).toList();

                if (newAnalyticsRecords.isEmpty()) {
                        log.warn("No new analytics records to save.");
                        throw new CustomGlobalErrorHandling.DataIntegrityViolationException();
                }

                var authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()
                                && authentication.getPrincipal() instanceof User user) {
                        newAnalyticsRecords.forEach(analyticRecord -> analyticRecord.setOwnerUserId(user));
                        List<Analytic> persistedRecords = analyticsRepository.saveAll(newAnalyticsRecords);

                        List<AnalyticsDTO> failedRecords = AnalyticObjectValidationComponent
                                        .filterFailedRecords(persistedRecords).stream()
                                        .map(AnalyticMapper::toRecord).toList();

                        analyticFailedNotificationComponent.processFailedRecordsNotification(failedRecords);

                }

        }

        @Override
        public AnalyticsDTO validateAnalyticByUser(Long id) {
                Analytic analytic = analyticsRepository.getReferenceById(id);

                var authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()
                                && authentication.getPrincipal() instanceof User user) {
                        analytic.setValidatorUserId(user);

                }
                return AnalyticMapper.toRecord(analytic);
        }

        @Override
        public String convertLevel(String level) {
                if (level == null) {
                        throw new IllegalArgumentException("Level cannot be null");
                }
                return level.toUpperCase();
        }

        // ==================== BASIC QUERY OPERATIONS ====================

        @Override
        public List<AnalyticsDTO> findAnalyticsByDate(LocalDateTime dateStart,
                        LocalDateTime dateEnd) {
                List<AnalyticsDTO> results =
                                analyticsRepository.findByDateBetween(dateStart, dateEnd).stream()
                                                .map(AnalyticMapper::toRecord).toList();
                AnalyticObjectValidationComponent.validateResultsNotEmpty(results,
                                "No analytics found for the given date range");
                return results;
        }

        @Override
        public Page<AnalyticsDTO> findAnalyticsPagedByNameIn(List<String> names,
                        Pageable pageable) {
                return analyticsRepository.findByNameInPaged(names, pageable);
        }

        @Override
        @Cacheable("AnalyticsByNameWithPagination")
        public List<AnalyticsDTO> findAnalyticsByNameWithPagination(List<String> names, String name,
                        Pageable pageable) {

                if (names.contains(name)) {
                        List<AnalyticsDTO> analyticsList =
                                        analyticsRepository
                                                        .findByTestName(name.toUpperCase(),
                                                                        pageable)
                                                        .stream()
                                                        .map(AnalyticMapper::toRecord).toList();
                        AnalyticObjectValidationComponent.validateResultsNotEmpty(analyticsList,
                                        "No analytics found with the given name");
                        return analyticsList;
                }
                throw new CustomGlobalErrorHandling.ResourceNotFoundException(
                                "AnalyticsDTO by name not found");
        }

        @Override
        @Cacheable(value = "analyticsByNameAndDateRange",
                        key = "{#names.hashCode(), #dateStart, #dateEnd, #pageable.pageNumber, #pageable.pageSize}")
        public Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetween(List<String> names,
                        LocalDateTime dateStart,
                        LocalDateTime dateEnd, Pageable pageable) {
                return analyticsRepository.findByNameInAndDateBetweenPaged(names, dateStart,
                                dateEnd, pageable);
        }

        @Override
        public Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetweenWithLinks(List<String> names,
                        LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                return analyticsRepository.findByNameInAndDateBetweenPaged(names, dateStart,
                                dateEnd, pageable);
        }

        // ==================== LEVEL-SPECIFIC QUERY OPERATIONS ====================

        @Override
        public List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable, String name,
                        String level) {
                List<AnalyticsDTO> analyticsList =
                                analyticsRepository
                                                .findByNameAndLevel(pageable, name.toUpperCase(),
                                                                level)
                                                .stream()
                                                .map(AnalyticMapper::toRecord).toList();
                AnalyticObjectValidationComponent.validateResultsNotEmpty(analyticsList,
                                "No analytics found for the given name and level");
                return analyticsList;
        }

        @Override
        public Page<AnalyticsDTO> findAnalyticsByNameInByLevel(List<String> names, String level,
                        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
                Page<AnalyticsDTO> results =
                                analyticsRepository.findByNameInAndLevelAndDateBetween(names, level,
                                                startDate, endDate, pageable);
                AnalyticObjectValidationComponent.validateResultsNotEmpty(results.getContent(),
                                "No analytics found for the given parameters with pagination");
                return results;
        }

        // ==================== ANALYTICS CALCULATION OPERATIONS ====================

        @Override
        public AnalyticsWithCalcDTO findAnalyticsByNameLevelDate(String name, String level,
                        LocalDateTime dateStart,
                        LocalDateTime dateEnd, Pageable pageable) {
                List<AnalyticsDTO> results = analyticsRepository
                                .findByNameAndLevelAndDateBetween(name, level, dateStart, dateEnd,
                                                pageable)
                                .stream()
                                .map(AnalyticMapper::toRecord).toList();
                MeanAndStdDeviationDTO calcSdAndMean =
                                StatisticsCalculatorComponent
                                                .calculateMeanAndStandardDeviation(results);

                AnalyticsWithCalcDTO analyticsWithCalcDTO =
                                new AnalyticsWithCalcDTO(results, calcSdAndMean);

                AnalyticObjectValidationComponent.validateResultsNotEmpty(results,
                                "No analytics found for the given name, level, dateStart, dateEnd -> parameters");
                return analyticsWithCalcDTO;
        }

        @Override
        public List<GroupedValuesByLevelDTO> findGroupedAnalyticsByLevel(String name,
                        LocalDateTime startDate,
                        LocalDateTime endDate, Pageable pageable) {

                List<AnalyticsDTO> records = analyticsRepository
                                .findByNameAndDateBetweenGroupByLevel(name, startDate, endDate,
                                                pageable)
                                .stream()
                                .map(AnalyticMapper::toRecord).toList();

                AnalyticObjectValidationComponent.validateResultsNotEmpty(records,
                                "No analytics found for the given name and date between parameters");

                return records.stream().collect(Collectors.groupingBy(AnalyticsDTO::level))
                                .entrySet().stream()
                                .map(entry -> new GroupedValuesByLevelDTO(entry.getKey(),
                                                entry.getValue()))
                                .toList();
        }

        @Override
        public List<GroupedResultsByLevelDTO> findAnalyticsWithGroupedResults(String name,
                        LocalDateTime startDate,
                        LocalDateTime endDate, Pageable pageable) {

                List<GroupedValuesByLevelDTO> analytics =
                                findGroupedAnalyticsByLevel(name, startDate, endDate, pageable);

                Map<String, MeanAndStdDeviationDTO> statsByLevel = analytics.stream()
                                .collect(Collectors.toMap(
                                                GroupedValuesByLevelDTO::level,
                                                group -> StatisticsCalculatorComponent
                                                                .calculateMeanAndStandardDeviation(
                                                                                group.values())));

                return analytics.stream().map(analytic -> new GroupedResultsByLevelDTO(analytic,
                                new GroupedMeanAndStdByLevelDTO(analytic.level(),
                                                Collections.singletonList(statsByLevel
                                                                .get(analytic.level())))))
                                .toList();
        }

        // ==================== DATA UPDATE OPERATIONS ====================

        @Override
        public void updateAnalyticsMeanByNameAndLevelAndLevelLot(String name, String level,
                        String levelLot,
                        double mean) {
                analyticsRepository.updateMeanByNameAndLevelAndLevelLot(name, level, levelLot,
                                mean);
        }
}
