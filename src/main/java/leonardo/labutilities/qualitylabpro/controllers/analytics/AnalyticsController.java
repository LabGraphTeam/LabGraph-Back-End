package leonardo.labutilities.qualitylabpro.controllers.analytics;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import leonardo.labutilities.qualitylabpro.dtos.analytics.*;
import leonardo.labutilities.qualitylabpro.services.analytics.AnalyticsHelperService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/generic-analytics")
@RestController()
public abstract class AnalyticsController extends AnalyticsHelperController {

	public AnalyticsController(AnalyticsHelperService analyticsHelperService) {
		super(analyticsHelperService);
	}

	@GetMapping()
	public abstract ResponseEntity<CollectionModel<EntityModel<AnalyticsRecord>>> getAllAnalytics(
			@PageableDefault(sort = "date",
					direction = Sort.Direction.DESC) @ParameterObject Pageable pageable);


	@GetMapping("/date-range")
	public abstract ResponseEntity<Page<AnalyticsRecord>> getAnalyticsDateBetween(
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate, @PageableDefault(sort = "date",
					direction = Sort.Direction.DESC) @ParameterObject Pageable pageable);

	@GetMapping("/level-date-range")
	public abstract ResponseEntity<Page<AnalyticsRecord>> getAllAnalyticsByLevelDateRange(
			@RequestParam String level, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate, @ParameterObject Pageable pageable);

	@GetMapping("/name-and-level-date-range")
	public abstract ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsByNameAndLevelDateRange(
			@RequestParam String name, @RequestParam String level,
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate);

	@GetMapping("/mean-standard-deviation")
	public abstract ResponseEntity<MeanAndStdDeviationRecord> getMeanAndStandardDeviation(
			@RequestParam String name, @RequestParam String level,
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate);
}


