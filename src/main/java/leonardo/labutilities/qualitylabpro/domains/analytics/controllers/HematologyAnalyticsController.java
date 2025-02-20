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
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.AvailableHematologyAnalytics;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsNameAndLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.HematologyAnalyticService;

@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/hematology-analytics")
@RestController()
public class HematologyAnalyticsController extends AbstractAnalyticsController {

	private static final List<String> names = AvailableHematologyAnalytics.DEFAULT_HEMATO_ANALYTICS;
	private final HematologyAnalyticService hematologyAnalyticsService;

	public HematologyAnalyticsController(HematologyAnalyticService hematologyAnalyticsService) {
		super(hematologyAnalyticsService);
		this.hematologyAnalyticsService = hematologyAnalyticsService;
	}

	@Override
	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalytics(
			@PageableDefault(sort = "measurementDate",
					direction = Sort.Direction.DESC) @ParameterObject Pageable pageable) {
		return this.getAllAnalyticsWithLinks(names, pageable);
	}

	@Override
	@GetMapping("/date-range")
	public ResponseEntity<Page<AnalyticsDTO>> getAnalyticsDateBetween(
			@ParameterObject AnalyticsDateRangeParamsDTO params,
			@PageableDefault(sort = "measurementDate", direction = Sort.Direction.DESC,
					size = 1500) @ParameterObject Pageable pageable) {
		return ResponseEntity
				.ok(this.hematologyAnalyticsService.findAnalyticsByNameInAndDateBetween(names,
						params.startDate(), params.endDate(), pageable));
	}

	@Override
	@GetMapping("/level-date-range")
	public ResponseEntity<Page<AnalyticsDTO>> getAllAnalyticsByLevelDateRange(
			@ParameterObject AnalyticsLevelDateRangeParamsDTO params,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		return ResponseEntity.ok(this.hematologyAnalyticsService.findAnalyticsByNameInByLevel(names,
				params.level(), params.startDate(), params.endDate(), pageable));
	}

	@Override
	@GetMapping("/mean-standard-deviation")
	public ResponseEntity<MeanAndStdDeviationDTO> getMeanAndStandardDeviation(
			@RequestParam String name, @RequestParam String level,
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		return ResponseEntity.ok(this.hematologyAnalyticsService
				.calculateMeanAndStandardDeviation(name, level, startDate, endDate, pageable));
	}

	// V2
	@Override
	@GetMapping("/name-and-level-date-range")
	public ResponseEntity<AnalyticsWithCalcDTO> getAllAnalyticsByNameAndLevelDateRangeV2(
			@ParameterObject AnalyticsNameAndLevelDateRangeParamsDTO params,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		return ResponseEntity.ok(this.hematologyAnalyticsService.findAnalyticsByNameLevelDate(
				params.name(), params.level(), params.startDate(), params.endDate(), pageable));
	}

}
