package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import leonardo.labutilities.qualitylabpro.domains.analytics.components.AnalyticFailedNotificationComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.AnalyticObjectValidationComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.StatisticsCalculatorComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.AnalyticErrorMessages;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.utils.AuthenticatedUserProvider;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for analytics helper operations. Provides functionality
 * for CRUD operations, analytics calculations, and various query operations
 * related to analytics data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticHelperService implements IAnalyticHelperService {

        private final AnalyticsRepository analyticsRepository;
        private final IAnalyticValidationService analyticsValidationService;
        private final AnalyticFailedNotificationComponent analyticFailedNotificationComponent;

        @Override
        public AnalyticsDTO findOneById(Long id) {
                return analyticsRepository.findById(id)
                                .map(AnalyticMapper::toRecord)
                                .orElseThrow(() -> new CustomGlobalErrorHandling.ResourceNotFoundException(
                                                AnalyticErrorMessages.ANALYTICS_NOT_FOUND_BY_ID));
        }

        @Override
        public void deleteAnalyticsById(Long id) {
                if (!analyticsRepository.existsById(id)) {
                        throw new CustomGlobalErrorHandling.ResourceNotFoundException(
                                        AnalyticErrorMessages.ANALYTICS_NOT_FOUND_BY_ID);
                }
                analyticsRepository.deleteById(id);
        }

        @Override
        public List<AnalyticsDTO> saveNewAnalyticsRecords(List<AnalyticsDTO> valuesOfLevelsList) {
                List<Analytic> newAnalyticsRecords = valuesOfLevelsList.stream()
                                .filter(analyticsValidationService::isNewAnalyticRecord)
                                .map(AnalyticMapper::toNewEntity)
                                .toList();

                if (newAnalyticsRecords.isEmpty()) {
                        log.warn("No new analytics records to save.");
                        throw new CustomGlobalErrorHandling.AnalyticsDataIntegrityViolationException();
                }

                User currentUser = AuthenticatedUserProvider.getCurrentAuthenticatedUser();
                newAnalyticsRecords.forEach(analyticRecord -> analyticRecord.setOwnerUserId(currentUser));

                List<Analytic> persistedRecords = analyticsRepository.saveAll(newAnalyticsRecords);

                List<AnalyticsDTO> failedRecords = AnalyticObjectValidationComponent
                                .filterFailedRecords(persistedRecords).stream()
                                .map(AnalyticMapper::toRecord)
                                .toList();

                analyticFailedNotificationComponent.processFailedRecordsNotification(failedRecords);

                return persistedRecords.stream()
                                .map(AnalyticMapper::toRecord)
                                .toList();
        }

        @Override
        public AnalyticsDTO validateAnalyticByUser(Long id) {
                Analytic analytic = analyticsRepository.getReferenceById(id);
                Optional.ofNullable(
                                AuthenticatedUserProvider.getCurrentAuthenticatedUser())
                                .ifPresent(analytic::setValidatorUserId);

                return AnalyticMapper.toRecord(analytic);
        }

        @Override
        public AnalyticsDTO updateDescription(Long id, String description) {
                return analyticsRepository.findById(id)
                                .map(analytic -> {
                                        analytic.setDescription(description);
                                        return AnalyticMapper.toRecord(analyticsRepository.save(analytic));
                                })
                                .orElseThrow(() -> new CustomGlobalErrorHandling.ResourceNotFoundException(
                                                AnalyticErrorMessages.ANALYTICS_NOT_FOUND_BY_ID));
        }

        @Override
        public String convertLevel(String level) {
                Assert.notNull(level, "Level cannot be null");
                return level.toUpperCase();
        }

        @Override
        public List<AnalyticsDTO> findAnalyticsByDate(LocalDateTime dateStart, LocalDateTime dateEnd) {
                List<AnalyticsDTO> results = analyticsRepository.findByDateBetween(dateStart, dateEnd).stream()
                                .map(AnalyticMapper::toRecord)
                                .toList();

                AnalyticObjectValidationComponent.validateResultsNotEmpty(results,
                                AnalyticErrorMessages.NO_ANALYTICS_FOR_DATE_RANGE);
                return results;
        }

        @Override
        public Page<AnalyticsDTO> findAnalyticsPagedByNameIn(List<String> names, Pageable pageable) {
                return analyticsRepository.findByNameInPaged(names, pageable);
        }

        @Override
        public List<AnalyticsDTO> findAnalyticsByNameWithPagination(List<String> names, String name,
                        Pageable pageable) {
                if (!names.contains(name)) {
                        throw new CustomGlobalErrorHandling.ResourceNotFoundException(
                                        AnalyticErrorMessages.ANALYTICS_NOT_FOUND_BY_NAME);
                }

                List<AnalyticsDTO> analyticsList = analyticsRepository
                                .findByTestName(name.toUpperCase(), pageable)
                                .stream()
                                .map(AnalyticMapper::toRecord)
                                .toList();

                AnalyticObjectValidationComponent.validateResultsNotEmpty(analyticsList,
                                "No analytics found with the given name");
                return analyticsList;
        }

        @Override
        public Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetween(
                        List<String> names, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {

                Page<AnalyticsDTO> analytics = analyticsRepository.findByNameInAndDateBetweenPaged(
                                names, dateStart, dateEnd, pageable);

                AnalyticObjectValidationComponent.validateResultsNotEmpty(
                                analytics.getContent(), AnalyticErrorMessages.NO_ANALYTICS_FOR_PAGINATION);

                return analytics;
        }

        @Override
        public Page<AnalyticsDTO> findUnvalidAnalyticsByNameInAndDateBetween(
                        List<String> names, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {

                Page<AnalyticsDTO> analytics = analyticsRepository.findUnvalidByNameInAndDateBetweenPaged(
                                names, dateStart, dateEnd, pageable);

                AnalyticObjectValidationComponent.validateResultsNotEmpty(
                                analytics.getContent(), AnalyticErrorMessages.NO_ANALYTICS_FOR_PAGINATION);

                return analytics;
        }

        @Override
        public Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetweenWithLinks(
                        List<String> names, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
                return analyticsRepository.findByNameInAndDateBetweenPaged(names, dateStart, dateEnd, pageable);
        }

        @Override
        public List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable, String name, String level) {
                List<AnalyticsDTO> analyticsList = analyticsRepository
                                .findByNameAndLevel(pageable, name.toUpperCase(), level)
                                .stream()
                                .map(AnalyticMapper::toRecord)
                                .toList();

                AnalyticObjectValidationComponent.validateResultsNotEmpty(analyticsList,
                                AnalyticErrorMessages.NO_ANALYTICS_FOR_NAME_LEVEL);
                return analyticsList;
        }

        @Override
        public Page<AnalyticsDTO> findAnalyticsByNameInByLevel(
                        List<String> names, String level, LocalDateTime startDate, LocalDateTime endDate,
                        Pageable pageable) {

                Page<AnalyticsDTO> results = analyticsRepository.findByNameInAndLevelAndDateBetween(
                                names, level, startDate, endDate, pageable);

                AnalyticObjectValidationComponent.validateResultsNotEmpty(
                                results.getContent(), AnalyticErrorMessages.NO_ANALYTICS_FOR_PAGINATION);

                return results;
        }

        @Override
        public Page<AnalyticsDTO> findUnvalidAnalyticsByNameInByLevel(
                        List<String> names, String level, LocalDateTime startDate, LocalDateTime endDate,
                        Pageable pageable) {

                Page<AnalyticsDTO> results = analyticsRepository.findUnValidByNameInAndLevelAndDateBetween(
                                names, level, startDate, endDate, pageable);

                AnalyticObjectValidationComponent.validateResultsNotEmpty(
                                results.getContent(), AnalyticErrorMessages.NO_ANALYTICS_FOR_PAGINATION);

                return results;
        }

        @Override
        public AnalyticsWithCalcDTO findAnalyticsByNameLevelDate(
                        String name, String level, LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {

                List<AnalyticsDTO> results = analyticsRepository
                                .findByNameAndLevelAndDateBetween(name, level, dateStart, dateEnd, pageable)
                                .stream()
                                .map(AnalyticMapper::toRecord)
                                .toList();

                MeanAndStdDeviationDTO calcSdAndMean = StatisticsCalculatorComponent
                                .calculateMeanAndStandardDeviation(results);

                AnalyticsWithCalcDTO analyticsWithCalcDTO = new AnalyticsWithCalcDTO(results, calcSdAndMean);

                AnalyticObjectValidationComponent.validateResultsNotEmpty(results,
                                AnalyticErrorMessages.NO_ANALYTICS_FOR_PARAMETERS);
                return analyticsWithCalcDTO;
        }

        @Override
        public List<GroupedValuesByLevelDTO> findGroupedAnalyticsByLevel(
                        String name, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

                List<AnalyticsDTO> records = analyticsRepository
                                .findByNameAndDateBetweenGroupByLevel(name, startDate, endDate, pageable)
                                .stream()
                                .map(AnalyticMapper::toRecord)
                                .toList();

                AnalyticObjectValidationComponent.validateResultsNotEmpty(records,
                                AnalyticErrorMessages.NO_ANALYTICS_FOR_NAME_DATE);

                return records.stream()
                                .collect(Collectors.groupingBy(AnalyticsDTO::level))
                                .entrySet()
                                .stream()
                                .map(entry -> new GroupedValuesByLevelDTO(entry.getKey(), entry.getValue()))
                                .toList();
        }

        @Override
        public List<GroupedResultsByLevelDTO> findAnalyticsWithGroupedResults(
                        String name, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

                List<GroupedValuesByLevelDTO> analytics = findGroupedAnalyticsByLevel(
                                name, startDate, endDate, pageable);

                Map<String, MeanAndStdDeviationDTO> statsByLevel = analytics.stream()
                                .collect(Collectors.toMap(
                                                GroupedValuesByLevelDTO::level,
                                                group -> StatisticsCalculatorComponent
                                                                .calculateMeanAndStandardDeviation(group.values())));

                return analytics.stream()
                                .map(analytic -> new GroupedResultsByLevelDTO(
                                                analytic,
                                                new GroupedMeanAndStdByLevelDTO(
                                                                analytic.level(),
                                                                Collections.singletonList(
                                                                                statsByLevel.get(analytic.level())))))
                                .toList();
        }

        @Override
        public void updateAnalyticsMeanByNameAndLevelAndLevelLot(
                        String name, String level, String levelLot, double mean) {
                analyticsRepository.updateMeanByNameAndLevelAndLevelLot(name, level, levelLot, mean);
        }
}
