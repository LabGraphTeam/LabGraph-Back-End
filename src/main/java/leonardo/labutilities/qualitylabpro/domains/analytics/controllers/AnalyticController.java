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
import org.springframework.http.HttpStatus;
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
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsListValidationByUserDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsNameAndLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.ComparativeErrorStatisticsParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.UpdateAnalyticsMeanDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ComparativeErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticHelperService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticStatisticsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
class AnalyticController extends AnalyticHelperController {

	private final List<String> names;

	protected AnalyticController(AnalyticHelperService analyticHelperService,
			AnalyticStatisticsService analyticsStatisticsService, List<String> names) {
		super(analyticHelperService, analyticsStatisticsService);
		this.names = names;
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deleteAnalyticsResultById(@PathVariable Long id) {
		analyticHelperService.deleteAnalyticsById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PatchMapping("/{id}/validate")
	@Transactional
	public ResponseEntity<AnalyticsDTO> patchValidateAnalyticByUser(@PathVariable Long id) {
		var respose = analyticHelperService.validateAnalyticByUser(id);
		return ResponseEntity.status(HttpStatus.OK).body(respose);
	}

	@PatchMapping("/validate")
	@Transactional
	public ResponseEntity<List<AnalyticsDTO>> patchValidateAnalyticListByUser(
			@RequestBody AnalyticsListValidationByUserDTO analyticsListValidationByUserDTO) {
		var respose = analyticHelperService.validateAnalyticListByUser(analyticsListValidationByUserDTO.ids());

		return ResponseEntity.status(HttpStatus.OK).body(respose);
	}

	@PatchMapping("/{id}/description")
	@Transactional
	public ResponseEntity<AnalyticsDTO> patchDescriptionAnalyticByUser(@PathVariable Long id,
			@RequestBody String description) {
		var respose = analyticHelperService.updateDescription(id, description);

		return ResponseEntity.status(HttpStatus.OK).body(respose);
	}

	@PostMapping
	@Transactional
	public ResponseEntity<List<AnalyticsDTO>> postAnalytics(
			@RequestBody @Valid List<AnalyticsDTO> values) {
		var response = analyticHelperService.saveNewAnalyticsRecords(values);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalytics(
			@ParameterObject Pageable pageable) {
		return getAllAnalyticsWithLinks(names, pageable);
	}

	@GetMapping("/name")
	public ResponseEntity<List<AnalyticsDTO>> getAnalyticsByNameWithPagination(@RequestParam String name,
			@PageableDefault(sort = "measurementDate", direction = Sort.Direction.DESC,
					size = 200) @ParameterObject Pageable pageable) {
		var response = analyticHelperService.findAnalyticsByNameWithPagination(names, name, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/date-range")
	public ResponseEntity<Page<AnalyticsDTO>> getAnalyticsDateBetween(
			@ParameterObject AnalyticsDateRangeParamsDTO params,
			@PageableDefault(sort = "measurementDate", direction = Sort.Direction.DESC,
					size = 2500) @ParameterObject Pageable pageable) {
		var result = analyticHelperService.findAnalyticsByNameInAndDateBetween(names,
				params.startDate(), params.endDate(), pageable);

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/date-range/unvalid")
	public ResponseEntity<Page<AnalyticsDTO>> getUnvalidAnalyticsDateBetween(
			@ParameterObject AnalyticsDateRangeParamsDTO params,
			@PageableDefault(sort = "measurementDate", direction = Sort.Direction.DESC,
					size = 1500) @ParameterObject Pageable pageable) {

		var result = analyticHelperService.findUnvalidAnalyticsByNameInAndDateBetween(names,
				params.startDate(), params.endDate(), pageable);

		log.debug("Found {} analytics entries in date range", result.getTotalElements());

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/level-date-range")
	public ResponseEntity<Page<AnalyticsDTO>> getAllAnalyticsByLevelDateRange(
			@ParameterObject AnalyticsLevelDateRangeParamsDTO params,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		var result = analyticHelperService.findAnalyticsByNameInByLevel(names,
				analyticHelperService.convertLevel(params.level()), params.startDate(),
				params.endDate(), pageable);

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/level-date-range/unvalid")
	public ResponseEntity<Page<AnalyticsDTO>> getAllUnvalidAnalyticsByLevelDateRange(
			@ParameterObject AnalyticsLevelDateRangeParamsDTO params,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		var result = analyticHelperService.findUnvalidAnalyticsByNameInByLevel(names,
				analyticHelperService.convertLevel(params.level()), params.startDate(),
				params.endDate(), pageable);

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/mean-standard-deviation")
	public ResponseEntity<MeanAndStdDeviationDTO> getMeanAndStandardDeviation(
			@RequestParam String name, @RequestParam String level,
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		var result = analyticsStatisticsService.calculateMeanAndStandardDeviation(name,
				analyticHelperService.convertLevel(level), startDate, endDate, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/name-and-level-date-range")
	public ResponseEntity<AnalyticsWithCalcDTO> getAllAnalyticsByNameAndLevelDateRange(
			@ParameterObject AnalyticsNameAndLevelDateRangeParamsDTO params,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		var result = analyticHelperService.findAnalyticsByNameLevelDate(params.name(),
				analyticHelperService.convertLevel(params.level()), params.startDate(),
				params.endDate(), pageable);

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/grouped-by-level")
	public ResponseEntity<List<GroupedResultsByLevelDTO>> getGroupedByLevel(
			@RequestParam String name, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		List<GroupedResultsByLevelDTO> result = analyticHelperService
				.findAnalyticsWithGroupedResults(name, startDate, endDate, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@GetMapping("/grouped-by-level/mean-deviation")
	public ResponseEntity<List<GroupedMeanAndStdByLevelDTO>> getMeanAndDeviationGrouped(
			@RequestParam String name, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		List<GroupedMeanAndStdByLevelDTO> response = analyticsStatisticsService
				.calculateGroupedMeanAndStandardDeviation(name, startDate, endDate, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/error-statistics")
	public ResponseEntity<List<ErrorStatisticsDTO>> getErrorStatistics(
			@RequestParam String level,
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate) {

		List<ErrorStatisticsDTO> response = analyticsStatisticsService
				.calculateErrorStatistics(names, analyticHelperService.convertLevel(level), startDate, endDate);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/error-statistics/comparative")
	public ResponseEntity<ComparativeErrorStatisticsDTO> getErrorStatisticsComparative(
			@ParameterObject ComparativeErrorStatisticsParamsDTO params) {

		ComparativeErrorStatisticsDTO response = analyticsStatisticsService
				.calculateComparativeErrorStatistics(params.analyticName(),
						analyticHelperService.convertLevel(params.analyticLevel()),
						params.firstStartDate(),
						params.firstEndDate(),
						params.secondStartDate(),
						params.secondEndDate());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PatchMapping()
	public ResponseEntity<Void> updateAnalyticsMean(
			@Valid @RequestBody UpdateAnalyticsMeanDTO updateAnalyticsMeanDTO) {

		analyticHelperService.updateAnalyticsMeanByNameAndLevelAndLevelLot(
				updateAnalyticsMeanDTO.name(),
				analyticHelperService.convertLevel(updateAnalyticsMeanDTO.level()),
				updateAnalyticsMeanDTO.levelLot(), updateAnalyticsMeanDTO.mean());

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
