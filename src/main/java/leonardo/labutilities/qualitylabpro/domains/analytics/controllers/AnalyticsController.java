package leonardo.labutilities.qualitylabpro.domains.analytics.controllers;

import java.time.LocalDateTime;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsNameAndLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.UpdateAnalyticsMeanDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticHelperService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticsStatisticsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
public class AnalyticsController extends AnalyticsHelperController {

	private final List<String> names;

	protected AnalyticsController(AnalyticHelperService analyticHelperService,
			AnalyticsStatisticsService analyticsStatisticsService, List<String> names) {
		super(analyticHelperService, analyticsStatisticsService);
		this.names = names;
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deleteAnalyticsResultById(@PathVariable Long id) {
		analyticHelperService.deleteAnalyticsById(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/validate")
	@Transactional
	public ResponseEntity<AnalyticsDTO> patchValidateAnalyticByUser(@PathVariable Long id) {
		var respose = analyticHelperService.validateAnalyticByUser(id);
		return ResponseEntity.ok(respose);
	}

	@PostMapping
	@Transactional
	public ResponseEntity<List<AnalyticsDTO>> postAnalytics(
			@RequestBody @Valid List<AnalyticsDTO> values) {
		analyticHelperService.saveNewAnalyticsRecords(values);
		return ResponseEntity.status(201).build();
	}

	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalytics(
			@PageableDefault(sort = "measurementDate",
					direction = Sort.Direction.DESC) @ParameterObject Pageable pageable) {
		log.info("Fetching all analytics with pagination: {}", pageable);
		return getAllAnalyticsWithLinks(names, pageable);
	}

	@GetMapping("/name")
	public List<AnalyticsDTO> getAnalyticsByNameWithPagination(@RequestParam String name,
			@PageableDefault(sort = "measurementDate", direction = Sort.Direction.DESC,
					size = 200) @ParameterObject Pageable pageable) {
		return analyticHelperService.findAnalyticsByNameWithPagination(names, name, pageable);
	}

	@GetMapping("/date-range")
	public ResponseEntity<Page<AnalyticsDTO>> getAnalyticsDateBetween(
			@ParameterObject AnalyticsDateRangeParamsDTO params,
			@PageableDefault(sort = "measurementDate", direction = Sort.Direction.DESC,
					size = 1500) @ParameterObject Pageable pageable) {

		log.info("Fetching analytics between {} and {} with pagination: {}", params.startDate(),
				params.endDate(), pageable);

		var result = analyticHelperService.findAnalyticsByNameInAndDateBetween(names,
				params.startDate(), params.endDate(), pageable);

		log.debug("Found {} analytics entries in date range", result.getTotalElements());

		return ResponseEntity.ok(result);
	}

	@GetMapping("/level-date-range")
	public ResponseEntity<Page<AnalyticsDTO>> getAllAnalyticsByLevelDateRange(
			@ParameterObject AnalyticsLevelDateRangeParamsDTO params,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		log.info("Fetching analytics for level {} between {} and {}", params.level(),
				params.startDate(), params.endDate());

		var result = analyticHelperService.findAnalyticsByNameInByLevel(names,
				analyticHelperService.convertLevel(params.level()), params.startDate(),
				params.endDate(), pageable);

		log.debug("Found {} analytics entries for level {}", result.getTotalElements(),
				params.level());

		return ResponseEntity.ok(result);
	}

	@GetMapping("/mean-standard-deviation")
	public ResponseEntity<MeanAndStdDeviationDTO> getMeanAndStandardDeviation(
			@RequestParam String name, @RequestParam String level,
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		log.info("Calculating mean and standard deviation for {} at level {} between {} and {}",
				name, level, startDate, endDate);

		var result = analyticsStatisticsService.calculateMeanAndStandardDeviation(name,
				analyticHelperService.convertLevel(level), startDate, endDate, pageable);

		log.debug("Calculated statistics: mean={}, stdDev={}", result.mean(),
				result.standardDeviation());

		return ResponseEntity.ok(result);
	}

	@GetMapping("/name-and-level-date-range")
	public ResponseEntity<AnalyticsWithCalcDTO> getAllAnalyticsByNameAndLevelDateRange(
			@ParameterObject AnalyticsNameAndLevelDateRangeParamsDTO params,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		log.info("Fetching analytics for name={} level={} between {} and {}", params.name(),
				params.level(), params.startDate(), params.endDate());

		var result = analyticHelperService.findAnalyticsByNameLevelDate(params.name(),
				analyticHelperService.convertLevel(params.level()), params.startDate(),
				params.endDate(), pageable);

		log.debug("Retrieved analytics with calculated values: analytics={}, calcs={}",
				result.analyticsDTO(), result.calcMeanAndStdDTO());

		return ResponseEntity.ok(result);
	}

	@GetMapping("/grouped-by-level")
	public ResponseEntity<List<GroupedResultsByLevelDTO>> getGroupedByLevel(
			@RequestParam String name, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		List<GroupedResultsByLevelDTO> groupedData = analyticHelperService
				.findAnalyticsWithGroupedResults(name, startDate, endDate, pageable);

		return ResponseEntity.ok(groupedData);
	}

	@GetMapping("/grouped-by-level/mean-deviation")
	public ResponseEntity<List<GroupedMeanAndStdByLevelDTO>> getMeanAndDeviationGrouped(
			@RequestParam String name, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		List<GroupedMeanAndStdByLevelDTO> groupedData = analyticsStatisticsService
				.calculateGroupedMeanAndStandardDeviation(name, startDate, endDate, pageable);

		return ResponseEntity.ok(groupedData);
	}

	@PatchMapping()
	public ResponseEntity<Void> updateAnalyticsMean(
			@Valid @RequestBody UpdateAnalyticsMeanDTO updateAnalyticsMeanDTO) {

		analyticHelperService.updateAnalyticsMeanByNameAndLevelAndLevelLot(
				updateAnalyticsMeanDTO.name(),
				analyticHelperService.convertLevel(updateAnalyticsMeanDTO.level()),
				updateAnalyticsMeanDTO.levelLot(), updateAnalyticsMeanDTO.mean());

		return ResponseEntity.noContent().build();
	}
}
