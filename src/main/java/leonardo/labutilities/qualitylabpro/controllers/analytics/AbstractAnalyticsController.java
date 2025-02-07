package leonardo.labutilities.qualitylabpro.controllers.analytics;

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
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.services.analytics.AbstractAnalyticHelperService;


@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/generic-analytics")
@RestController()
public abstract class AbstractAnalyticsController extends AnalyticsHelperController {

	protected AbstractAnalyticsController(AbstractAnalyticHelperService analyticHelperService) {
		super(analyticHelperService);
	}

	@GetMapping()
	public abstract ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalytics(
			@PageableDefault(size = 100, sort = "date",
					direction = Sort.Direction.DESC) @ParameterObject Pageable pageable);


	@GetMapping("/date-range")
	public abstract ResponseEntity<Page<AnalyticsDTO>> getAnalyticsDateBetween(
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 1500, sort = "date",
					direction = Sort.Direction.DESC) @ParameterObject Pageable pageable);

	@GetMapping("/level-date-range")
	public abstract ResponseEntity<Page<AnalyticsDTO>> getAllAnalyticsByLevelDateRange(
			@RequestParam String level, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable);

	@GetMapping("/name-and-level-date-range")
	public abstract ResponseEntity<List<AnalyticsDTO>> getAllAnalyticsByNameAndLevelDateRange(
			@RequestParam String name, @RequestParam String level,
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable);

	@GetMapping("/mean-standard-deviation")
	public abstract ResponseEntity<MeanAndStdDeviationDTO> getMeanAndStandardDeviation(
			@RequestParam String name, @RequestParam String level,
			@RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable);
}


