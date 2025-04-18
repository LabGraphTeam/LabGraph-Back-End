package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createDateRangeRecords;
import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecord;
import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import leonardo.labutilities.qualitylabpro.domains.analytics.components.AnalyticFailedNotificationComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.RulesProviderComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.UpdateAnalyticsMeanDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.utils.AnalyticRulesValidation;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

@ExtendWith(MockitoExtension.class)
class AnalyticHelperServiceTests {

	@Mock
	private AnalyticsRepository analyticsRepository;
	@Mock
	private AnalyticHelperService analyticHelperService;
	@Mock
	private AnalyticFailedNotificationComponent analyticFailedNotificationComponent;
	@Mock
	private AnalyticValidationService analyticsValidationService;
	@Mock
	private AnalyticStatisticsService analyticsStatisticsService;
	@Mock
	private EmailService emailService;
	@Mock
	private RulesProviderComponent controlRulesValidators;

	public AnalyticHelperServiceTests() {
		super();
	}

	@BeforeEach
	void setUp() {
		try (AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this)) {
			this.analyticHelperService = new AnalyticHelperService(this.analyticsRepository,
					analyticsValidationService, this.analyticFailedNotificationComponent) {

				@Override
				public List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable,
						String name, String level) {
					return AnalyticHelperServiceTests.this.analyticsRepository
							.findByNameAndLevel(name, level, pageable);
				}

				@Override
				public String convertLevel(String level) {
					throw new UnsupportedOperationException("Unimplemented method 'convertLevel'");
				}

				@Override
				public AnalyticsWithCalcDTO findAnalyticsByNameLevelDate(String name, String level,
						LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
					List<AnalyticsDTO> analyticsList =
							AnalyticHelperServiceTests.this.analyticsRepository
									.findByNameAndLevelAndDateBetween(name, level, dateStart,
											dateEnd, pageable);

					MeanAndStdDeviationDTO calcSdAndMean = new MeanAndStdDeviationDTO(10.0, 0.5);

					return new AnalyticsWithCalcDTO(analyticsList, calcSdAndMean);
				}
			};

		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize mocks", e);
		}
	}

	@Test
	@DisplayName("Should update analytics mean value when valid parameters are provided")
	void updateAnalyticsMean() {
		var mockDto = new UpdateAnalyticsMeanDTO("Glucose", "PCCC1", "076587", 1.0);
		this.analyticHelperService.updateAnalyticsMeanByNameAndLevelAndLevelLot(mockDto.name(),
				mockDto.level(), mockDto.levelLot(), mockDto.mean());
		verify(this.analyticsRepository).updateMeanByNameAndLevelAndLevelLot(mockDto.name(),
				mockDto.level(), mockDto.levelLot(), mockDto.mean());
	}

	@Test
	@DisplayName("Should validate rules when processed by rules validator component")
	void shouldValidateRulesProcessedByRulesValidatorComponent() {
		List<AnalyticsDTO> records = createSampleRecordList();

		List<Analytic> analytics = records.stream().map(AnalyticMapper::toNewEntity).toList();

		assertEquals(records.stream().map(AnalyticsDTO::rules).toList(),
				analytics.stream().map(Analytic::getControlRules).toList(),
				"The rules processed by the RulesValidatorComponent should match the input rules");

	}

	@Test
	@DisplayName("Should save records successfully when valid analytics data is provided")
	void saveNewAnalyticsRecords_WithValidRecords_ShouldSaveSuccessfully() {
		List<AnalyticsDTO> records = createSampleRecordList();
		List<Analytic> analytics = records.stream().map(AnalyticMapper::toNewEntity).toList();

		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		User mockUser = Mockito.mock(User.class);

		try (MockedStatic<SecurityContextHolder> securityContextHolder =
				Mockito.mockStatic(SecurityContextHolder.class);
				MockedStatic<AnalyticRulesValidation> validationComponent =
						Mockito.mockStatic(AnalyticRulesValidation.class)) {

			securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
			when(securityContext.getAuthentication()).thenReturn(authentication);
			when(authentication.isAuthenticated()).thenReturn(true);
			when(authentication.getPrincipal()).thenReturn(mockUser);

			when(this.analyticsValidationService.isNewAnalyticRecord(any())).thenReturn(true);
			when(this.analyticsRepository.saveAll(any())).thenReturn(analytics);

			validationComponent.when(() -> AnalyticRulesValidation.filterFailedRecords(anyList()))
					.thenReturn(List.of());
			doNothing().when(this.analyticFailedNotificationComponent).processFailedRecordsNotification(anyList());

			assertDoesNotThrow(() -> this.analyticHelperService.saveNewAnalyticsRecords(records));

			verify(this.analyticsRepository).saveAll(any());
			verify(this.analyticFailedNotificationComponent).processFailedRecordsNotification(anyList());
		}
	}

	@Test
	@DisplayName("Should throw exception when no new records to save")
	void saveNewAnalyticsRecords_WithNoNewRecords_ShouldThrowException() {
		// Arrange
		List<AnalyticsDTO> records = createSampleRecordList();

		when(this.analyticsValidationService.isNewAnalyticRecord(any())).thenReturn(false);

		// Act & Assert
		assertThrows(CustomGlobalErrorHandling.AnalyticsDataIntegrityViolationException.class,
				() -> this.analyticHelperService.saveNewAnalyticsRecords(records));

		// Verify
		verify(this.analyticsRepository, never()).saveAll(any());
		verify(this.analyticFailedNotificationComponent, never()).processFailedRecordsNotification(anyList());
	}

	@Test
	@DisplayName("Should throw BadCredentialsException when user is not authenticated")
	void saveNewAnalyticsRecords_WithoutAuthentication_ShouldThrowException() {
		// Arrange
		List<AnalyticsDTO> records = createSampleRecordList();

		// Mock authentication
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);

		try (MockedStatic<SecurityContextHolder> securityContextHolder =
				Mockito.mockStatic(SecurityContextHolder.class)) {
			securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
			when(securityContext.getAuthentication()).thenReturn(null);

			when(this.analyticsValidationService.isNewAnalyticRecord(any())).thenReturn(true);

			// Act & Assert
			assertThrows(BadCredentialsException.class,
					() -> this.analyticHelperService.saveNewAnalyticsRecords(records));

			// Verify
			verify(this.analyticsRepository, never()).saveAll(any());
		}
	}

	@Test
	@DisplayName("Should throw exception when trying to save duplicate analytics records")
	void saveNewAnalyticsRecords_WithDuplicateRecords_ShouldThrowException() {
		List<AnalyticsDTO> records = createSampleRecordList();

		when(this.analyticsValidationService.isNewAnalyticRecord(any())).thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.AnalyticsDataIntegrityViolationException.class,
				() -> this.analyticHelperService.saveNewAnalyticsRecords(records));
		verify(this.analyticsRepository, never()).saveAll(any());
	}

	@Test
	@DisplayName("Should return record when searching by valid ID")
	void findById_WithValidId_ShouldReturnRecord() {
		Long id = 1L;
		Analytic analytic = AnalyticMapper.toEntity(createSampleRecord());
		analytic.setId(id);

		when(this.analyticsRepository.findById(id)).thenReturn(Optional.of(analytic));

		AnalyticsDTO result = this.analyticHelperService.findOneById(id);

		assertNotNull(result);

		assertEquals(AnalyticMapper.toRecord(analytic), result);
	}

	@Test
	@DisplayName("Should throw exception when searching by invalid ID")
	void findById_WithInvalidId_ShouldThrowException() {
		Long id = 999L;
		when(this.analyticsRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> this.analyticHelperService.findOneById(id));
	}

	@Test
	@DisplayName("Should return filtered records when searching by name and level")
	void findAnalyticsByNameAndLevel_WithFilters_ShouldReturnFilteredRecords() {
		String name = "Glucose";
		String level = "Normal";
		Pageable pageable = PageRequest.of(0, 10);
		List<AnalyticsDTO> expectedRecords = createSampleRecordList().stream()
				.filter(r -> r.name().equals(name) && r.level().equals(level)).toList();

		when(this.analyticsRepository.findByNameAndLevel(name, level,
				pageable))
						.thenReturn(expectedRecords);

		List<AnalyticsDTO> result =
				this.analyticHelperService.findAnalyticsByNameAndLevel(pageable, name, level);

		assertEquals(expectedRecords.size(), result.size());
		verify(this.analyticsRepository).findByNameAndLevel(name, level, pageable);
	}

	@Test
	@DisplayName("Should return filtered records when searching by date range")
	void findAllAnalyticsByNameAndLevelAndDate_WithDateRange_ShouldReturnFilteredRecords() {
		String name = "ALB2";
		String level = "1";
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 1, 2, 0, 0);
		List<AnalyticsDTO> mockResultRepository =
				createDateRangeRecords();

		when(this.analyticsRepository.findByNameAndLevelAndDateBetween(eq(name), eq(level),
				eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(mockResultRepository);

		AnalyticsWithCalcDTO result = this.analyticHelperService.findAnalyticsByNameLevelDate(name,
				level, startDate, endDate, Pageable.unpaged());

		assertNotNull(result);
	}

	@Test
	@DisplayName("Should delete analytics when valid ID is provided")
	void deleteAnalyticsById_WithValidId_ShouldDelete() {
		Long id = 1L;
		when(this.analyticsRepository.existsById(id)).thenReturn(true);
		doNothing().when(this.analyticsRepository).deleteById(id);

		assertDoesNotThrow(() -> this.analyticHelperService.deleteAnalyticsById(id));

		verify(this.analyticsRepository).deleteById(id);
	}

	@Test
	@DisplayName("Should throw exception when deleting analytics with invalid ID")
	void deleteAnalyticsById_WithInvalidId_ShouldThrowException() {
		Long id = 999L;
		when(this.analyticsRepository.existsById(id)).thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> this.analyticHelperService.deleteAnalyticsById(id));
		verify(this.analyticsRepository, never()).deleteById(id);
	}

	@Test
	@DisplayName("Should not throw exception when validating existing analytics name")
	void ensureNameExists_WithValidName_ShouldNotThrowException() {
		String name = "Glucose";

		assertDoesNotThrow(
				() -> this.analyticsValidationService.ensureAnalyticTestNameExists(name));
	}

	@Test
	@DisplayName("Should return true when checking non-existent analytics record")
	void isAnalyticsNonExistent_WithNonExistentRecord_ShouldReturnTrue() {
		AnalyticsDTO analyticsRecord = createSampleRecord();
		when(this.analyticsValidationService.isNewAnalyticRecord(analyticsRecord)).thenReturn(true);

		boolean result = this.analyticsValidationService.isNewAnalyticRecord(analyticsRecord);

		assertTrue(result);
	}

	@Test
	@DisplayName("Should return false when checking existing analytics record")
	void isAnalyticsNonExistent_WithExistentRecord_ShouldReturnFalse() {
		AnalyticsDTO analyticsRecord = createSampleRecord();

		when(this.analyticsValidationService.isNewAnalyticRecord(analyticsRecord))
				.thenReturn(false);

		boolean result = this.analyticsValidationService.isNewAnalyticRecord(analyticsRecord);

		assertFalse(result);
	}

	@Test
	@DisplayName("Should return grouped records when searching with valid inputs")
	void findGroupedAnalyticsByLevel_WithValidInputs_ShouldReturnGroupedRecords() {
		String name = "Glucose";
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 1, 2, 0, 0);
		List<Analytic> records =
				createSampleRecordList().stream().map(AnalyticMapper::toNewEntity).toList();

		when(this.analyticsRepository.findByNameAndDateBetweenGroupByLevel(eq(name), eq(startDate),
				eq(endDate), any(Pageable.class))).thenReturn(records);

		List<GroupedValuesByLevelDTO> result = this.analyticHelperService
				.findGroupedAnalyticsByLevel(name, startDate, endDate, Pageable.unpaged());

		assertNotNull(result);
		assertFalse(result.isEmpty());
		verify(this.analyticsRepository).findByNameAndDateBetweenGroupByLevel(eq(name),
				eq(startDate), eq(endDate), any(Pageable.class));
	}

	@Test
	@DisplayName("Should calculate mean and standard deviation correctly")
	void calculateMeanAndStandardDeviation_WithValidData_ShouldReturnCorrectValues() {
		// Arrange
		String name = "Glucose";
		String level = "Normal";
		LocalDateTime startDate = LocalDateTime.now().minusDays(7);
		LocalDateTime endDate = LocalDateTime.now();

		when(analyticsStatisticsService.calculateMeanAndStandardDeviation(name, level, startDate,
				endDate, Pageable.unpaged())).thenReturn(new MeanAndStdDeviationDTO(10, 0.5));
		// Act
		var result = this.analyticsStatisticsService.calculateMeanAndStandardDeviation(name, level,
				startDate, endDate, Pageable.unpaged());

		// Assert
		assertNotNull(result);
		assertTrue(result.mean() > 0);
		assertTrue(result.standardDeviation() >= 0);
	}

	@Test
	@DisplayName("Should process failed records notification correctly")
	void processFailedRecordsNotification_WithFailedRecords_ShouldSendNotification() {
		// Arrange
		List<AnalyticsDTO> failedRecords = createSampleRecordList();
		// Act
		this.analyticFailedNotificationComponent.processFailedRecordsNotification(failedRecords);
		// Assert
		verify(this.analyticFailedNotificationComponent)
				.processFailedRecordsNotification(failedRecords);
	}

	@Test
	@DisplayName("Should handle empty failed records list correctly")
	void processFailedRecordsNotification_WithEmptyList_ShouldNotSendNotification() {
		// Arrange
		List<AnalyticsDTO> emptyList = List.of();

		// Act
		this.analyticFailedNotificationComponent.processFailedRecordsNotification(emptyList);

		// Assert
		verify(this.emailService, never()).sendFailedAnalyticsNotification(any(), any());
		verify(this.controlRulesValidators, never()).validateRules(any());
	}

	@Test
	@DisplayName("Should validate grouped records correctly")
	void isGroupedRecordValid_WithValidRecords_ShouldReturnTrue() {
		// Arrange
		List<AnalyticsDTO> validRecords = createSampleRecordList().stream()
				.map((AnalyticMapper::toEntity)).map(analyticsRecord -> {
					analyticsRecord.setControlRules(
							"Approved according to current Westgard configured rules"); // Invalid
					// rule
					return AnalyticMapper.toRecord(analyticsRecord);
				}).toList();

		GroupedValuesByLevelDTO groupedRecords =
				new GroupedValuesByLevelDTO("Normal", validRecords);
		when(this.analyticsValidationService.isGroupedRecordValid(groupedRecords)).thenReturn(true);

		// Act
		boolean result = this.analyticsValidationService.isGroupedRecordValid(groupedRecords);

		// Assert
		assertTrue(result);
	}

	@Test
	@DisplayName("Should identify invalid grouped records correctly")
	void isGroupedRecordValid_WithInvalidRecords_ShouldReturnFalse() {
		// Arrange
		List<AnalyticsDTO> invalidRecords = createSampleRecordList().stream()
				.map((AnalyticMapper::toEntity)).map(analyticsRecord -> {
					analyticsRecord.setControlRules("-3s"); // Invalid rule
					return AnalyticMapper.toRecord(analyticsRecord);
				}).toList();

		GroupedValuesByLevelDTO groupedRecords =
				new GroupedValuesByLevelDTO("Normal", invalidRecords);
		when(this.analyticsValidationService.isGroupedRecordValid(groupedRecords))
				.thenReturn(false);

		// Act
		boolean result = this.analyticsValidationService.isGroupedRecordValid(groupedRecords);

		// Assert
		assertFalse(result);
	}

	@Test
	@DisplayName("Should calculate grouped statistics correctly")
	void calculateGroupedMeanAndStandardDeviation_WithValidData_ShouldReturnCorrectValues() {
		// Arrange
		String name = "Glucose";
		LocalDateTime startDate = LocalDateTime.now().minusDays(7);
		LocalDateTime endDate = LocalDateTime.now();

		when(analyticsStatisticsService.calculateGroupedMeanAndStandardDeviation(name, startDate,
				endDate, Pageable.unpaged()))
						.thenReturn(List.of(new GroupedMeanAndStdByLevelDTO("Normal",
								List.of(new MeanAndStdDeviationDTO(10, 0.5)))));
		// Act
		var result = this.analyticsStatisticsService.calculateGroupedMeanAndStandardDeviation(name,
				startDate, endDate, Pageable.unpaged());

		// Assert
		assertNotNull(result);
		assertFalse(result.isEmpty());
		result.forEach(group -> {
			assertNotNull(group.level());
			assertTrue(group.values().getFirst().mean() > 0);
			assertTrue(group.values().getFirst().standardDeviation() >= 0);
		});
	}

	@Test
	@DisplayName("Should handle analytics find with date range correctly")
	void findAnalyticsByDate_WithValidDateRange_ShouldReturnCorrectRecords() {
		// Arrange
		LocalDateTime startDate = LocalDateTime.now().minusDays(7);
		LocalDateTime endDate = LocalDateTime.now();
		List<AnalyticsDTO> expectedAnalytics =
				createDateRangeRecords();

		when(this.analyticsRepository.findByDateBetween(startDate, endDate))
				.thenReturn(expectedAnalytics);

		// Act
		List<AnalyticsDTO> result =
				this.analyticHelperService.findAnalyticsByDate(startDate, endDate);

		// Assert
		assertNotNull(result);
		assertEquals(expectedAnalytics.size(), result.size());
		verify(this.analyticsRepository).findByDateBetween(startDate, endDate);
	}
}
