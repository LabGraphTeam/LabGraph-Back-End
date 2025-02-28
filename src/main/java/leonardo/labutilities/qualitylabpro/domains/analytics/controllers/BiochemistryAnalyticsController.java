package leonardo.labutilities.qualitylabpro.domains.analytics.controllers;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.AvailableBiochemistryAnalytics;

import leonardo.labutilities.qualitylabpro.domains.analytics.services.BiochemistryAnalyticService;
import lombok.extern.slf4j.Slf4j;

@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/biochemistry-analytics")
@Slf4j
@RestController()
public class BiochemistryAnalyticsController extends AnalyticsController {

	private static final List<String> names = AvailableBiochemistryAnalytics.DEFAULT_BIO_ANALYTICS;

	public BiochemistryAnalyticsController(
			BiochemistryAnalyticService biochemistryAnalyticsService) {
		super(biochemistryAnalyticsService, names);
	}
}
