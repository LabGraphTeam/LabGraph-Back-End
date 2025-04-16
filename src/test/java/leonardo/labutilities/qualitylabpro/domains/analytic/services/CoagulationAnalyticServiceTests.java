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
import leonardo.labutilities.qualitylabpro.domains.analytics.services.CoagulationAnalyticService;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@ExtendWith(MockitoExtension.class)
class CoagulationAnalyticServiceTests extends AnalyticServiceTests {

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

	private final CoagulationAnalyticService coagulationAnalyticService;

	CoagulationAnalyticServiceTests() {
		this.coagulationAnalyticService = new CoagulationAnalyticService(this.analyticsRepository,
				this.analyticFailedNotificationComponent, this.analyticsValidationService);
	}

	@Override
	@Test
	void convertLevel_ShouldReturnCorrectLevel() {
		assertEquals("Normal C. Assayed", this.coagulationAnalyticService.convertLevel("1"));
		assertEquals("Low Abn C. Assayed", this.coagulationAnalyticService.convertLevel("2"));
	}

	@Override
	@Test
	void convertLevel_ShouldThrowException_WhenInvalidLevel() {
		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> this.coagulationAnalyticService.convertLevel("3"));
	}
}
