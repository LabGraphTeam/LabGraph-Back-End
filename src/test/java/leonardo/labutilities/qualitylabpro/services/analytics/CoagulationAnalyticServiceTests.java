package leonardo.labutilities.qualitylabpro.services.analytics;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import leonardo.labutilities.qualitylabpro.dtos.analytics.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.entities.Analytic;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.components.ControlRulesValidators;
import leonardo.labutilities.qualitylabpro.utils.constants.AvailableAnalyticsNames;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;
import leonardo.labutilities.qualitylabpro.utils.mappers.AnalyticMapper;

@ExtendWith(MockitoExtension.class)
class CoagulationAnalyticServiceTests {

	@Mock
	private AnalyticsRepository analyticsRepository;
	@Mock
	private EmailService emailService;
	@Mock
	private ControlRulesValidators controlRulesValidators;

	private CoagulationAnalyticService coagulationAnalyticService;
	private Pageable pageable;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	@BeforeEach
	void setUp() {
		this.coagulationAnalyticService = new CoagulationAnalyticService(this.analyticsRepository,
				this.emailService, this.controlRulesValidators);
		this.pageable = PageRequest.of(0, 10);
		this.startDate = LocalDateTime.now().minusDays(7);
		this.endDate = LocalDateTime.now();
	}

	@Test
	void findAnalyticsByNameInByLevel_ShouldReturnPageOfAnalytics() {
		List<String> names = AvailableAnalyticsNames.ALL_ANALYTICS;
		List<AnalyticsDTO> expectedList = createSampleRecordList();
		Page<AnalyticsDTO> expectedPage = new PageImpl<>(expectedList);

		when(this.analyticsRepository.findByNameInAndLevelAndDateBetween(any(), any(), any(), any(),
				any())).thenReturn(expectedPage);

		Page<AnalyticsDTO> result = this.coagulationAnalyticService.findAnalyticsByNameInByLevel(
				names, "1", this.startDate, this.endDate, this.pageable);

		assertNotNull(result);
		assertEquals(expectedPage, result);
	}

	@Test
	void findAnalyticsByNameAndLevel_ShouldReturnAnalyticsList() {
		String name = "PT";
		List<Analytic> analytics =
				createSampleRecordList().stream().map(AnalyticMapper::toEntity).toList();

		when(this.analyticsRepository.existsByName(name)).thenReturn(true);
		when(this.analyticsRepository.findByNameAndLevel(any(), any(), any()))
				.thenReturn(analytics);

		List<AnalyticsDTO> expectedList = createSampleRecordList();
		List<AnalyticsDTO> result = this.coagulationAnalyticService
				.findAnalyticsByNameAndLevel(this.pageable, name, "1");

		assertNotNull(result);
		assertEquals(expectedList, result);
	}

	@Test
	void findAnalyticsByNameAndLevelAndDate_ShouldReturnAnalyticsList() {
		String name = "TAP-20";
		List<Analytic> analytics =
				createSampleRecordList().stream().map(AnalyticMapper::toEntity).toList();

		when(this.analyticsRepository.findByNameAndLevelAndDateBetween(any(), any(), any(), any(),
				any())).thenReturn(analytics);

		List<AnalyticsDTO> result =
				this.coagulationAnalyticService.findAnalyticsByNameAndLevelAndDate(name, "1",
						this.startDate, this.endDate, this.pageable);

		var expectedAnalytics = createSampleRecordList();
		assertNotNull(result);
		assertEquals(expectedAnalytics, result);
	}

	@Test
	void findAnalyticsByNameLevelDate_ShouldReturnAnalyticsWithCalcDTO() {
		String name = "PT";
		List<Analytic> mockAnalytics =
				createSampleRecordList().stream().map(AnalyticMapper::toEntity).toList();

		when(this.analyticsRepository.findByNameAndLevelAndDateBetween(name, "Normal C. Assayed",
				this.startDate, this.endDate, this.pageable)).thenReturn(mockAnalytics);

		AnalyticsWithCalcDTO result = this.coagulationAnalyticService.findAnalyticsByNameLevelDate(
				name, "1", this.startDate, this.endDate, this.pageable);

		assertNotNull(result);
		assertNotNull(result.analyticsDTO());
		assertNotNull(result.calcMeanAndStdDTO());
		assertEquals(mockAnalytics.size(), result.analyticsDTO().size());
	}

	@Test
	void convertLevel_ShouldReturnCorrectLevel() {
		assertEquals("Normal C. Assayed", this.coagulationAnalyticService.convertLevel("1"));
		assertEquals("Low Abn C. Assayed", this.coagulationAnalyticService.convertLevel("2"));
	}

	@Test
	void convertLevel_ShouldThrowException_WhenInvalidLevel() {
		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> this.coagulationAnalyticService.convertLevel("3"));
	}
}
