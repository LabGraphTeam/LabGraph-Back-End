package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import org.springframework.stereotype.Service;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.AnalyticFailedNotificationComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@Service
public class BiochemistryAnalyticService extends AnalyticHelperService {

	public BiochemistryAnalyticService(AnalyticsRepository analyticsRepository,
			AnalyticFailedNotificationComponent analyticFailedNotificationComponent,
			AnalyticsValidationService analyticsValidationService) {
		super(analyticsRepository, analyticFailedNotificationComponent, analyticsValidationService);
	}

	@Override
	public String convertLevel(String inputLevel) {
		return switch (inputLevel) {
			case "1" -> "PCCC1";
			case "2" -> "PCCC2";
			default -> throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"Level not found.");
		};
	}
}
