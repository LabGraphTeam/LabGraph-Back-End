package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import org.springframework.stereotype.Service;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.RulesProviderComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@Service
public class CoagulationAnalyticService extends AnalyticHelperService {

	public CoagulationAnalyticService(AnalyticsRepository analyticsRepository,
			EmailService emailService, RulesProviderComponent controlRulesValidators) {
		super(analyticsRepository, emailService, controlRulesValidators);
	}

	@Override
	public String convertLevel(String inputLevel) {
		return switch (inputLevel) {
			case "1" -> "Normal C. Assayed";
			case "2" -> "Low Abn C. Assayed";
			default -> throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"Level not found.");
		};
	}
}
