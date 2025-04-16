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
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticValidationService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.BiochemistryAnalyticService;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@ExtendWith(MockitoExtension.class)
class BiochemistryAnalyticServiceTests extends AnalyticServiceTests {

	@Mock
	private AnalyticsRepository analyticsRepository;

	@Mock
	private EmailService emailService;

	@Mock
	private AnalyticValidationService analyticsValidationService;

	@Mock
	private AnalyticFailedNotificationComponent analyticFailedNotificationComponent;

	@Mock
	private RulesProviderComponent controlRulesValidators;

	private final BiochemistryAnalyticService biochemistryAnalyticService;

	BiochemistryAnalyticServiceTests() {
		this.biochemistryAnalyticService = new BiochemistryAnalyticService(this.analyticsRepository,
				this.analyticFailedNotificationComponent, this.analyticsValidationService);
	}

	@Override
	@Test
	void convertLevel_ShouldReturnCorrectLevel() {
		assertEquals("PCCC1", this.biochemistryAnalyticService.convertLevel("1"));
		assertEquals("PCCC2", this.biochemistryAnalyticService.convertLevel("2"));
	}

	@Override
	@Test
	void convertLevel_ShouldThrowException_WhenInvalidLevel() {
		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> this.biochemistryAnalyticService.convertLevel("3"));
	}
}
