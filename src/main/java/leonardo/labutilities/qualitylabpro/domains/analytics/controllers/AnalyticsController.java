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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsNameAndLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticHelperService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/generic-analytics")
@RestController()
public class AnalyticsController extends AnalyticsHelperController {

	private final List<String> names;

	protected AnalyticsController(AnalyticHelperService analyticHelperService, List<String> names) {
		super(analyticHelperService);
		this.names = names;
	}

	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalytics(
			@PageableDefault(sort = "measurementDate",
					direction = Sort.Direction.DESC) @ParameterObject Pageable pageable) {
		log.info("Fetching all analytics with pagination: {}", pageable);
		return this.getAllAnalyticsWithLinks(names, pageable);
	}

	@GetMapping("/name")
	public List<AnalyticsDTO> getAnalyticsByNameWithPagination(@RequestParam String name,
			@PageableDefault(sort = "measurementDate", direction = Sort.Direction.DESC,
					size = 200) @ParameterObject Pageable pageable) {
		return this.analyticHelperService.findAnalyticsByNameWithPagination(names, name, pageable);
	}

	@GetMapping("/date-range")
	public ResponseEntity<Page<AnalyticsDTO>> getAnalyticsDateBetween(
			@ParameterObject AnalyticsDateRangeParamsDTO params,
			@PageableDefault(sort = "measurementDate", direction = Sort.Direction.DESC,
					size = 1500) @ParameterObject Pageable pageable) {
		log.info("Fetching analytics between {} and {} with pagination: {}", params.startDate(),
				params.endDate(), pageable);
		var result = this.analyticHelperService.findAnalyticsByNameInAndDateBetween(names,
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
		var result = this.analyticHelperService.findAnalyticsByNameInByLevel(names, params.level(),
				params.startDate(), params.endDate(), pageable);
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
		var result = this.analyticHelperService.calculateMeanAndStandardDeviation(name, level,
				startDate, endDate, pageable);
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
		var result = this.analyticHelperService.findAnalyticsByNameLevelDate(params.name(),
				params.level(), params.startDate(), params.endDate(), pageable);
		log.debug("Retrieved analytics with calculated values: analytics={}, calcs={}",
				result.analyticsDTO(), result.calcMeanAndStdDTO());
		return ResponseEntity.ok(result);
	}
}
