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
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.AvailableBiochemistryAnalytics;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsNameAndLevelDateRangeParamsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.BiochemistryAnalyticService;
import lombok.extern.slf4j.Slf4j;

@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("biochemistry-analytics")
@Slf4j
@RestController()
public class BiochemistryAnalyticsController extends AbstractAnalyticsController {

	private static final List<String> names = AvailableBiochemistryAnalytics.DEFAULT_BIO_ANALYTICS;

	private final BiochemistryAnalyticService biochemistryAnalyticsService;

	public BiochemistryAnalyticsController(
			BiochemistryAnalyticService biochemistryAnalyticsService) {
		super(biochemistryAnalyticsService);
		this.biochemistryAnalyticsService = biochemistryAnalyticsService;
	}

	@Override
	@GetMapping()
	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalytics(
			@PageableDefault(size = 100, sort = "measurementDate",
					direction = Sort.Direction.DESC) @ParameterObject Pageable pageable) {
		log.info("BiochemistryAnalyticsController::getAllAnalytics body: {}", pageable);
		return this.getAllAnalyticsWithLinks(names, pageable);
	}

	@Override
	@GetMapping("/date-range")
	public ResponseEntity<Page<AnalyticsDTO>> getAnalyticsDateBetween(
			@ParameterObject AnalyticsDateRangeParamsDTO params,
			@PageableDefault(sort = "measurementDate", direction = Sort.Direction.DESC,
					size = 1500) @ParameterObject Pageable pageable) {
		log.info(
				"BiochemistryAnalyticsController::getAnalyticsDateBetween params: {}, pageable: {}",
				params, pageable);
		return ResponseEntity
				.ok(this.biochemistryAnalyticsService.findAnalyticsByNameInAndDateBetween(names,
						params.startDate(), params.endDate(), pageable));
	}

	@Override
	@GetMapping("/level-date-range")
	public ResponseEntity<Page<AnalyticsDTO>> getAllAnalyticsByLevelDateRange(
			@ParameterObject AnalyticsLevelDateRangeParamsDTO params,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		log.info(
				"BiochemistryAnalyticsController::getAllAnalyticsByLevelDateRange params: {}, pageable: {}",
				params, pageable);
		return ResponseEntity.ok(this.biochemistryAnalyticsService.findAnalyticsByNameInByLevel(
				names, params.level(), params.startDate(), params.endDate(), pageable));
	}

	@Override
	@GetMapping("/mean-standard-deviation")
	public ResponseEntity<MeanAndStdDeviationDTO> getMeanAndStandardDeviation(
			@RequestParam String name, @RequestParam String level,
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		log.info(
				"BiochemistryAnalyticsController::getMeanAndStandardDeviation name: {}, level: {}, startDate: {}, endDate: {}, pageable: {}",
				name, level, startDate, endDate, pageable);
		return ResponseEntity.ok(this.biochemistryAnalyticsService
				.calculateMeanAndStandardDeviation(name, level, startDate, endDate, pageable));
	}

	@Override
	@GetMapping("/name-and-level-date-range")
	public ResponseEntity<AnalyticsWithCalcDTO> getAllAnalyticsByNameAndLevelDateRangeV2(
			@ParameterObject AnalyticsNameAndLevelDateRangeParamsDTO params,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		log.info(
				"BiochemistryAnalyticsController::getAllAnalyticsByNameAndLevelDateRangeV2 params: {}, pageable: {}",
				params, pageable);
		return ResponseEntity.ok(this.biochemistryAnalyticsService.findAnalyticsByNameLevelDate(
				params.name(), params.level(), params.startDate(), params.endDate(), pageable));
	}
}
