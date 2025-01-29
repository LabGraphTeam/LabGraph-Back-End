package leonardo.labutilities.qualitylabpro.services.analytics;

import leonardo.labutilities.qualitylabpro.dtos.analytics.*;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.components.ControlRulesValidators;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;
import leonardo.labutilities.qualitylabpro.utils.mappers.AnalyticMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static leonardo.labutilities.qualitylabpro.utils.blacklist.AnalyticsBlackList.BLACK_LIST;

@Slf4j
@Service
public abstract class AnalyticHelperService implements IAnalyticHelperService {

	private final AnalyticsRepository analyticsRepository;
	private final EmailService emailService;
	private final ControlRulesValidators controlRulesValidators;


	protected AnalyticHelperService(AnalyticsRepository analyticsRepository,
			EmailService emailService, ControlRulesValidators controlRulesValidators) {
		this.analyticsRepository = analyticsRepository;
		this.emailService = emailService;
		this.controlRulesValidators = controlRulesValidators;
	}

	// VALIDATION METHODS
	private void validateResultsNotEmpty(List<?> results, String message) {
		if (results == null || results.isEmpty()) {
			throw new CustomGlobalErrorHandling.ResourceNotFoundException(message);
		}
	}

	public boolean isAnalyticsNonExistent(AnalyticsDTO values) {
		return !analyticsRepository.existsByDateAndLevelAndName(values.date(), values.level(),
				values.name());
	}

	private boolean isRuleBroken(AnalyticsDTO analyticsDTO) {
		String rules = analyticsDTO.rules();
		return ("+3s".equals(rules) || "-3s".equals(rules) || "-2s".equals(rules)
				|| "+2s".equals(rules));
	}

	public void ensureNameExists(String name) {
		if (!analyticsRepository.existsByName(name.toUpperCase())) {
			throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"AnalyticsDTO by name not found");
		}
	}

	// STATISTICS METHODS
	private MeanAndStdDeviationDTO computeStatistics(List<Double> values) {
		double sum = values.stream().mapToDouble(Double::doubleValue).sum();
		int size = values.size();
		double mean = sum / size;
		double variance = values.stream().mapToDouble(value -> Math.pow(value - mean, 2)).average()
				.orElse(0.0);
		return new MeanAndStdDeviationDTO(mean, Math.sqrt(variance));
	}

	private List<Double> extractRecordValues(List<AnalyticsDTO> records) {
		return records.stream().map(AnalyticsDTO::value).toList();
	}


	// BUSINESS LOGIC METHODS
	public List<GroupedResultsByLevelDTO> findAnalyticsWithGroupedResults(String name,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		List<GroupedValuesByLevelDTO> analytics =
				findGroupedAnalyticsByLevel(name, startDate, endDate, pageable);
		Map<String, MeanAndStdDeviationDTO> statsByLevel =
				analytics.stream().collect(Collectors.toMap(GroupedValuesByLevelDTO::level,
						group -> computeStatistics(extractRecordValues(group.values()))));

		return analytics.stream()
				.map(analytic -> new GroupedResultsByLevelDTO(analytic,
						new GroupedMeanAndStdByLevelDTO(analytic.level(),
								Collections.singletonList(statsByLevel.get(analytic.level())))))
				.toList();
	}

	@Override
	public List<GroupedValuesByLevelDTO> findGroupedAnalyticsByLevel(String name,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		List<AnalyticsDTO> records = analyticsRepository
				.findByNameAndDateBetweenGroupByLevel(name, startDate, endDate, pageable).stream()
				.map(AnalyticMapper::toRecord).toList();
		validateResultsNotEmpty(records,
				"No analytics found for the given name and date between parameters");

		return records.stream().collect(Collectors.groupingBy(AnalyticsDTO::level)).entrySet()
				.stream()
				.map(entry -> new GroupedValuesByLevelDTO(entry.getKey(), entry.getValue()))
				.toList();
	}



	@Override
	public List<GroupedMeanAndStdByLevelDTO> returnMeanAndStandardDeviationForGroups(
			List<GroupedValuesByLevelDTO> records) {
		return records.stream()
				.map(group -> new GroupedMeanAndStdByLevelDTO(group.level(), Collections
						.singletonList(computeStatistics(extractRecordValues(group.values())))))
				.toList();
	}

	@Override
	public Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetweenWithLinks(List<String> names,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
		return analyticsRepository.findByNameInAndDateBetweenPaged(names, dateStart, dateEnd,
				pageable);
	}

	@Override
	public List<GroupedValuesByLevelDTO> findFilteredGroupedAnalytics(
			List<GroupedValuesByLevelDTO> groupedValuesByLevelDTO) {
		return groupedValuesByLevelDTO.stream().filter(this::isGroupedRecordValid)
				.map(groupedValue -> new GroupedValuesByLevelDTO(groupedValue.level(),
						groupedValue.values()))
				.toList();
	}

	@Override
	public void updateAnalyticsMeanByNameAndLevelAndLevelLot(String name, String level,
			String levelLot, double mean) {
		analyticsRepository.updateMeanByNameAndLevelAndLevelLot(name, level, levelLot, mean);
	}

	@Override
	public boolean isGroupedRecordValid(GroupedValuesByLevelDTO groupedValuesByLevelDTO) {
		return groupedValuesByLevelDTO.values().stream()
				.allMatch(groupedValue -> !Objects.equals(groupedValue.rules(), "+3s")
						&& !Objects.equals(groupedValue.rules(), "-3s"));
	}

	@Override
	public boolean isRecordValid(AnalyticsDTO analyticsDTO) {
		String rules = analyticsDTO.rules();
		return (!Objects.equals(rules, "+3s") || !Objects.equals(rules, "-3s"));
	}

	@Override
	public AnalyticsDTO findOneById(Long id) {
		return AnalyticMapper.toRecord(analyticsRepository.findById(id)
				.orElseThrow(() -> new CustomGlobalErrorHandling.ResourceNotFoundException(
						"AnalyticsDTO by id not found")));
	}

	@Override
	public void saveNewAnalyticsRecords(List<AnalyticsDTO> valuesOfLevelsList) {
		var newAnalytics = valuesOfLevelsList.stream().filter(this::isAnalyticsNonExistent)
				.map(AnalyticMapper::toEntity).toList();

		if (newAnalytics.isEmpty()) {
			log.warn("No new analytics records to save.");
			throw new CustomGlobalErrorHandling.DataIntegrityViolationException();
		}

		var analyticsList = analyticsRepository.saveAll(newAnalytics);

		List<AnalyticsDTO> notPassedList = analyticsList.stream().map(AnalyticMapper::toRecord)
				.filter(this::isRuleBroken).toList();

		if (notPassedList.isEmpty()) {
			return;
		}

		var filteredList = notPassedList.stream()
				.filter(notPassed -> !BLACK_LIST.contains(notPassed.name())).toList();

		try {
			var content = controlRulesValidators.validateRules(filteredList);
			emailService.sendFailedAnalyticsNotification(filteredList, content);
		} catch (Exception e) {
			log.error("Error sending identifier notification: {}", e.getMessage());
		}
	}



	@Override
	public Page<AnalyticsDTO> findAnalytics(Pageable pageable) {
		return analyticsRepository.findPaged(pageable);
	}

	@Override
	public List<AnalyticsDTO> findAnalyticsByNameWithPagination(Pageable pageable, String name) {
		List<AnalyticsDTO> analyticsList =
				analyticsRepository.findByName(name.toUpperCase(), pageable).stream()
						.map(AnalyticMapper::toRecord).toList();
		validateResultsNotEmpty(analyticsList, "No analytics found with the given name");
		return analyticsList;
	}

	@Override
	public Page<AnalyticsDTO> findAnalyticsByNameInByLevelBaseMethod(List<String> names,
			String level, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		Page<AnalyticsDTO> results = analyticsRepository.findByNameInAndLevelAndDateBetween(names,
				level, startDate, endDate, pageable);
		validateResultsNotEmpty(results.getContent(),
				"No analytics found for the given parameters with pagination");
		return results;
	}

	@Override
	public List<AnalyticsDTO> findAnalyticsByDate(LocalDateTime dateStart, LocalDateTime dateEnd) {
		List<AnalyticsDTO> results = analyticsRepository.findByDateBetween(dateStart, dateEnd)
				.stream().map(AnalyticMapper::toRecord).toList();
		validateResultsNotEmpty(results, "No analytics found for the given date range");
		return results;
	}

	@Override
	public Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetween(List<String> names,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
		return analyticsRepository.findByNameInAndDateBetween(names, dateStart, dateEnd, pageable);
	}

	@Override
	public void deleteAnalyticsById(Long id) {
		if (!analyticsRepository.existsById(id)) {
			throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"AnalyticsDTO by id not found");
		}
		analyticsRepository.deleteById(id);
	}

	public MeanAndStdDeviationDTO calculateMeanAndStandardDeviation(String name, String level,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
		List<AnalyticsDTO> values =
				findAnalyticsByNameAndLevelAndDate(name, level, dateStart, dateEnd, pageable)
						.stream().filter(this::isRecordValid).toList();
		return computeStatistics(extractRecordValues(values));
	}

	public List<GroupedMeanAndStdByLevelDTO> calculateGroupedMeanAndStandardDeviation(String name,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		List<AnalyticsDTO> records = analyticsRepository
				.findByNameAndDateBetweenGroupByLevel(name, startDate, endDate, pageable).stream()
				.map(AnalyticMapper::toRecord).toList();
		var values = records.stream().collect(Collectors.groupingBy(AnalyticsDTO::level)).entrySet()
				.stream()
				.map(entry -> new GroupedValuesByLevelDTO(entry.getKey(), entry.getValue()))
				.toList();

		return returnMeanAndStandardDeviationForGroups(values);
	}

	public List<AnalyticsDTO> findAnalyticsByNameIn(List<String> names, Pageable pageable) {
		return analyticsRepository.findByNameIn(names, pageable).stream()
				.map(AnalyticMapper::toRecord).toList();
	}

	public Page<AnalyticsDTO> findAnalyticsPagedByNameIn(List<String> names, Pageable pageable) {
		return analyticsRepository.findByNameInPaged(names, pageable);
	}

	public List<AnalyticsDTO> findAnalyticsByNameAndLevelWithPagination(Pageable pageable,
			String name, String level) {
		List<AnalyticsDTO> analyticsList =
				analyticsRepository.findByNameAndLevel(pageable, name.toUpperCase(), level).stream()
						.map(AnalyticMapper::toRecord).toList();
		validateResultsNotEmpty(analyticsList, "No analytics found for the given name and level");
		return analyticsList;
	}

	public List<AnalyticsDTO> findAnalyticsByNameLevelAndDate(String name, String level,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
		ensureNameExists(name);
		List<AnalyticsDTO> results = analyticsRepository
				.findByNameAndLevelAndDateBetween(name, level, dateStart, dateEnd, pageable)
				.stream().map(AnalyticMapper::toRecord).toList();
		validateResultsNotEmpty(results,
				"No analytics found for the given name, level, dateStart, dateEnd -> parameters");
		return results;
	}


}
