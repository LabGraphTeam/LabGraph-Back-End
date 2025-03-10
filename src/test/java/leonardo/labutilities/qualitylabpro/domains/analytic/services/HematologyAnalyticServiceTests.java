package leonardo.labutilities.qualitylabpro.domains.analytic.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.AnalyticFailedNotificationComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.RulesProviderComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticsValidationService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.HematologyAnalyticService;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@ExtendWith(MockitoExtension.class)
class HematologyAnalyticServiceTests extends AnalyticServiceTests {

	@Mock
	private AnalyticsRepository analyticsRepository;

	@Mock
	private EmailService emailService;

	@Mock
	private AnalyticsValidationService analyticsValidationService;

	@Mock
	private AnalyticFailedNotificationComponent analyticFailedNotificationComponent;

	@Mock
	private RulesProviderComponent controlRulesValidators;

	private final HematologyAnalyticService hematologyAnalyticService;

	HematologyAnalyticServiceTests() {
		this.hematologyAnalyticService = new HematologyAnalyticService(this.analyticsRepository,
				this.analyticFailedNotificationComponent, this.analyticsValidationService);
	}

	@Override
	@Test
	void convertLevel_ShouldReturnCorrectLevel() {
		assertEquals("low", this.hematologyAnalyticService.convertLevel("1"));
		assertEquals("normal", this.hematologyAnalyticService.convertLevel("2"));
		assertEquals("high", this.hematologyAnalyticService.convertLevel("3"));
	}

	@Override
	@Test
	void convertLevel_ShouldThrowException_WhenInvalidLevel() {
		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> this.hematologyAnalyticService.convertLevel("4"));
	}
}
