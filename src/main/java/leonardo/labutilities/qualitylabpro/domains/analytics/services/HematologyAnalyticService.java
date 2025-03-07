package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import org.springframework.stereotype.Service;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.AnalyticFailedNotificationComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@Service
public class HematologyAnalyticService extends AnalyticHelperService {

	public HematologyAnalyticService(AnalyticsRepository analyticsRepository,
			AnalyticFailedNotificationComponent analyticFailedNotificationComponent,
			AnalyticsValidationService analyticsValidationService) {
		super(analyticsRepository, analyticFailedNotificationComponent, analyticsValidationService);
	}

	@Override
	public String convertLevel(String inputLevel) {
		return switch (inputLevel) {
			case "1" -> "low";
			case "2" -> "normal";
			case "3" -> "high";
			default -> throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"Level not found.");
		};
	}
}
