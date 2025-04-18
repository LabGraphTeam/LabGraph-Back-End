package leonardo.labutilities.qualitylabpro.domains.analytics.controllers;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.AvailableCoagulationAnalytics;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticStatisticsService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.CoagulationAnalyticService;

@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/coagulation-analytics")
@RestController
public class CoagulationAnalyticController extends AnalyticController {

	private static final List<String> names = AvailableCoagulationAnalytics.DEFAULT_COAG_ANALYTICS;

	public CoagulationAnalyticController(CoagulationAnalyticService coagulationAnalyticsService,
			AnalyticStatisticsService analyticsStatisticsService) {
		super(coagulationAnalyticsService, analyticsStatisticsService, names);
	}
}
