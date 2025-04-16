package leonardo.labutilities.qualitylabpro.domains.analytics.controllers;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.AvailableHematologyAnalytics;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticStatisticsService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.HematologyAnalyticService;

@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/hematology-analytics")
@RestController
public class HematologyAnalyticController extends AnalyticController {

	private static final List<String> names = AvailableHematologyAnalytics.DEFAULT_HEMATO_ANALYTICS;

	public HematologyAnalyticController(HematologyAnalyticService hematologyAnalyticsService,
			AnalyticStatisticsService analyticsStatisticsService) {
		super(hematologyAnalyticsService, analyticsStatisticsService, names);
	}

}
