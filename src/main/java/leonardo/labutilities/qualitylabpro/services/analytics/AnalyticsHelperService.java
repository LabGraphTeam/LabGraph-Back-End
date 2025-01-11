package leonardo.labutilities.qualitylabpro.services.analytics;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import leonardo.labutilities.qualitylabpro.dtos.analytics.*;
import leonardo.labutilities.qualitylabpro.entities.Analytics;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;
import leonardo.labutilities.qualitylabpro.utils.mappers.AnalyticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class AnalyticsHelperService implements IAnalyticsHelperService {

	private final AnalyticsRepository analyticsRepository;
	private final Pageable pageable = PageRequest.of(0, 200);

	public AnalyticsHelperService(AnalyticsRepository analyticsRepository) {
		this.analyticsRepository = analyticsRepository;
	}

	@Override
	public void deleteAnalyticsById(Long id) {
		if (!analyticsRepository.existsById(id)) {
			throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"AnalyticsRecord by id not found");
		}
		analyticsRepository.deleteById(id);
	}

	public void updateAnalyticsMeanByNameAndLevelAndLevelLot(String name, String level,
															String levelLot, double mean) {
		analyticsRepository.updateMeanByNameAndLevelAndLevelLot(name, level, levelLot, mean);

	}

	private List<Double> extractRecordValues(List<AnalyticsRecord> records) {
		return records.stream().map(AnalyticsRecord::value).toList();
	}

	public void ensureNameExists(String name) {
		if (!analyticsRepository.existsByName(name.toUpperCase())) {
			throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"AnalyticsRecord by name not found");
		}
	}

	public boolean isAnalyticsNonExistent(AnalyticsRecord values) {
		return !analyticsRepository.existsByDateAndLevelAndName(values.date(),
				values.level(), values.name());
	}

	public List<AnalyticsRecord> validateAnalyticsNameExists(
			List<AnalyticsRecord> results) {
		if (results.isEmpty()) {
			throw new CustomGlobalErrorHandling.ResourceNotFoundException("Results not found.");
		}
		return results;
	}

	@Override
	public boolean isRecordValid(AnalyticsRecord record) {
		String rules = record.rules();
		return (!Objects.equals(rules, "+3s") && !Objects.equals(rules, "-3s"));
	}

	@Override
	public boolean isGroupedRecordValid(GroupedValuesByLevel record) {
		return record.values().stream()
				.allMatch(genericValuesRecord -> !Objects.equals(genericValuesRecord.rules(), "+3s")
						&& !Objects.equals(genericValuesRecord.rules(), "-3s"));
	}

	private void validateResultsNotEmpty(List<?> results, String message) {
		if (results == null || results.isEmpty()) {
			throw new CustomGlobalErrorHandling.ResourceNotFoundException(message);
		}
	}

	private MeanAndStdDeviationRecord computeStatistics(List<Double> values) {
		double sum = values.stream().mapToDouble(Double::doubleValue).sum();
		int size = values.size();
		double mean = sum / size;
		double variance = values.stream().mapToDouble(value -> Math.pow(value - mean, 2)).average()
				.orElse(0.0);
		return new MeanAndStdDeviationRecord(mean, Math.sqrt(variance));
	}

	@Override
	@Cacheable(cacheNames = "analytics-cache", key = "#records.hashCode()")
	public List<GroupedValuesByLevel> findFilteredGroupedAnalytics(
			List<GroupedValuesByLevel> records) {
		return records.stream().filter(this::isGroupedRecordValid)
				.map((GroupedValuesByLevel record) -> {
					List<AnalyticsRecord> filteredRecords = record.values();
					return new GroupedValuesByLevel(record.level(), filteredRecords);
				}).toList();
	}


	@Override
	@Cacheable(cacheNames = "analytics-cache",
			key = "#name + '-' + #startDate.toString() + '-' + #endDate.toString() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
	public List<GroupedValuesByLevel> findGroupedAnalyticsByLevel(String name,
			LocalDateTime startDate, LocalDateTime endDate) {
		List<AnalyticsRecord> records = analyticsRepository
				.findAllByNameAndDateBetweenGroupByLevel(name, startDate, endDate, pageable)
				.stream().map(AnalyticsMapper::toRecord).toList();
		validateResultsNotEmpty(records, "No analytics found for the given parameters");

		return records.stream().collect(Collectors.groupingBy(AnalyticsRecord::level))
				.entrySet().stream()
				.map(entry -> new GroupedValuesByLevel(entry.getKey(), entry.getValue())).toList();
	}

	@Cacheable(cacheNames = "analytics-cache",
			key = "#name + '-' + #startDate.toString() + '-' + #endDate.toString()")
	public List<GroupedResultsByLevel> findAnalyticsWithGroupedResults(String name,
			LocalDateTime startDate, LocalDateTime endDate) {
		List<GroupedValuesByLevel> analytics =
				findGroupedAnalyticsByLevel(name, startDate, endDate);
		Map<String, MeanAndStdDeviationRecord> statsByLevel =
				analytics.stream().collect(Collectors.toMap(GroupedValuesByLevel::level,
						group -> computeStatistics(extractRecordValues(group.values()))));

		return analytics.stream()
				.map(analytic -> new GroupedResultsByLevel(analytic,
						new GroupedMeanAndStdRecordByLevel(analytic.level(),
								Collections.singletonList(statsByLevel.get(analytic.level())))))
				.toList();
	}

	@Override
	@Cacheable(cacheNames = "analytics-cache", key = "#records.hashCode()")
	public List<GroupedMeanAndStdRecordByLevel> getMeanAndStandardDeviationForGroups(
			List<GroupedValuesByLevel> records) {
		return records.stream()
				.map(group -> new GroupedMeanAndStdRecordByLevel(group.level(), Collections
						.singletonList(computeStatistics(extractRecordValues(group.values())))))
				.toList();
	}

	@Cacheable(cacheNames = "analytics-cache",
			key = "#name + '-' + #level + '-' + #dateStart.toString() + '-' + #dateEnd.toString()")
	public MeanAndStdDeviationRecord calculateMeanAndStandardDeviation(String name, String level,
			LocalDateTime dateStart, LocalDateTime dateEnd) {
		List<AnalyticsRecord> values =
				findAllAnalyticsByNameAndLevelAndDate(name, level, dateStart, dateEnd).stream()
						.filter(this::isRecordValid).toList();
		return computeStatistics(extractRecordValues(values));
	}

	@Cacheable(cacheNames = "analytics-cache",
			key = "#name + '-' + #startDate.toString() + '-' + #endDate.toString()")
	public List<GroupedMeanAndStdRecordByLevel> calculateGroupedMeanAndStandardDeviation(
			String name, LocalDateTime startDate, LocalDateTime endDate) {
		List<AnalyticsRecord> records = analyticsRepository
				.findAllByNameAndDateBetweenGroupByLevel(name, startDate, endDate, pageable)
				.stream().map((AnalyticsMapper::toRecord)).toList();
		var values = records.stream().collect(Collectors.groupingBy(AnalyticsRecord::level))
				.entrySet().stream()
				.map(entry -> new GroupedValuesByLevel(entry.getKey(), entry.getValue())).toList();

		return getMeanAndStandardDeviationForGroups(values);
	}

	@Override
	@Cacheable(cacheNames = "analytics-cache",
			key = "#names.hashCode() + '-' + #dateStart.toString() + '-' + #dateEnd.toString()")
	public List<AnalyticsRecord> getAllByNameInAndDateBetween(List<String> names,
															  LocalDateTime dateStart, LocalDateTime dateEnd) {
		return analyticsRepository.findAllByNameInAndDateBetween(names, dateStart, dateEnd)
				.stream().map((AnalyticsMapper::toRecord)).toList();
	}

	@Cacheable(cacheNames = "analytics-cache",
			key = "#names.hashCode() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
	public List<AnalyticsRecord> getAllByNameIn(List<String> names, Pageable pageable) {
		return analyticsRepository
				.findAllByNameIn(names, pageable).stream().map((AnalyticsMapper::toRecord)).toList();
	}

	@Cacheable(cacheNames = "analytics-cache",
			key = "#names.hashCode() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
	public Page<AnalyticsRecord> getAllPagedByNameIn(List<String> names, Pageable pageable) {
		return analyticsRepository
				.findAllByNameInPaged(names, pageable);
	}


	@Override
	public abstract List<AnalyticsRecord> findAnalyticsByNameAndLevel(Pageable pageable,
																	  String name, String level);

	@Override
	public void saveNewAnalyticsRecords(List<AnalyticsRecord> valuesOfLevelsList) {
		List<leonardo.labutilities.qualitylabpro.entities.Analytics> newAnalytics =
				valuesOfLevelsList.stream().filter(this::isAnalyticsNonExistent)
						.map(AnalyticsMapper::toEntity)
						.collect(Collectors.toList());

		if (newAnalytics.isEmpty()) {
			throw new CustomGlobalErrorHandling.DataIntegrityViolationException();
		}
		analyticsRepository.saveAll(newAnalytics);
	}

	@Override
	@Cacheable(value = "name")
	public Page<AnalyticsRecord> findAll(Pageable pageable) {
		return analyticsRepository.findAllPaged(pageable);
	}


	@Override
	@Cacheable(cacheNames = "analytics-cache",
			key = "#name + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
	public List<AnalyticsRecord> findAnalyticsByNameWithPagination(Pageable pageable,
																   String name) {
		List<AnalyticsRecord> analyticsList =
				analyticsRepository
						.findAllByName(name.toUpperCase(), pageable).stream().map(AnalyticsMapper::toRecord).toList();
		validateResultsNotEmpty(analyticsList, "No analytics found with the given name");
		return analyticsList;
	}

	@Override
	@Cacheable(cacheNames = "analytics-cache", key = "#id")
	public AnalyticsRecord findById(Long id) {
		return AnalyticsMapper.toRecord(analyticsRepository.findById(id)
				.orElseThrow(() -> new CustomGlobalErrorHandling.ResourceNotFoundException(
						"AnalyticsRecord by id not found")));
	}

	@Cacheable(cacheNames = "analytics-cache",
			key = "#name + '-' + #level + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
	public List<AnalyticsRecord> findAnalyticsByNameAndLevelWithPagination(Pageable pageable, String name, String level) {
		List<AnalyticsRecord> analyticsList = analyticsRepository
				.findAllByNameAndLevel(pageable, name.toUpperCase(), level)
				.stream().map(AnalyticsMapper::toRecord).toList();
		validateResultsNotEmpty(analyticsList, "No analytics found for the given name and level");
		return analyticsList;
	}

	@Cacheable(cacheNames = "analytics-cache",
			key = "#name + '-' + #level + '-' + #dateStart.toString() + '-' + #dateEnd.toString()")
	List<AnalyticsRecord> findAnalyticsByNameLevelAndDate(String name, String level,
														  LocalDateTime dateStart, LocalDateTime dateEnd) {
		List<AnalyticsRecord> results = analyticsRepository
				.findAllByNameAndLevelAndDateBetween(name, level, dateStart, dateEnd, pageable)
				.stream().map(AnalyticsMapper::toRecord).toList();
		validateResultsNotEmpty(results, "No analytics found for the given parameters");
		return results;
	}

	@Override
	@Cacheable(cacheNames = "analytics-cache",
			key = "#dateStart.toString() + '-' + #dateEnd.toString()")
	public List<AnalyticsRecord> findAllAnalyticsByDate(LocalDateTime dateStart, LocalDateTime dateEnd) {
		List<AnalyticsRecord> results =
				analyticsRepository.findAllByDateBetween(dateStart, dateEnd)
						.stream().map(AnalyticsMapper::toRecord).toList();
		validateResultsNotEmpty(results, "No analytics found for the given date range");
		return results;
	}

}
