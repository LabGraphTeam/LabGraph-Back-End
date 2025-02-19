package leonardo.labutilities.qualitylabpro.services.analytics;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import leonardo.labutilities.qualitylabpro.dtos.analytics.requests.UpdateAnalyticsMeanDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.entities.Analytic;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.components.ControlRulesValidators;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;
import leonardo.labutilities.qualitylabpro.utils.mappers.AnalyticMapper;

@ExtendWith(MockitoExtension.class)
class AbstractAnalyticHelperServiceTests {

	@Mock
	private AnalyticsRepository analyticsRepository;
	@Mock
	private AbstractAnalyticHelperService abstractAnalyticHelperService;
	@Mock
	private EmailService emailService;
	@Mock
	private ControlRulesValidators controlRulesValidators;

	@BeforeEach
	void setUp() {
		try (AutoCloseable closeable = MockitoAnnotations.openMocks(this)) {
			this.abstractAnalyticHelperService = new AbstractAnalyticHelperService(
					this.analyticsRepository, this.emailService, this.controlRulesValidators) {

				@Override
				public List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable,
						String name, String level) {
					return AbstractAnalyticHelperServiceTests.this.analyticsRepository
							.findByNameAndLevel(pageable, name, level).stream()
							.map(AnalyticMapper::toRecord).toList();
				}

				@Override
				public List<AnalyticsDTO> findAnalyticsByNameAndLevelAndDate(String name,
						String level, LocalDateTime dateStart, LocalDateTime dateEnd,
						Pageable pageable) {
					return AbstractAnalyticHelperServiceTests.this.analyticsRepository
							.findByNameAndLevelAndDateBetween(name, level, dateStart, dateEnd,
									PageRequest.of(0, 200))
							.stream().map(AnalyticMapper::toRecord).toList();
				}

				@Override
				public String convertLevel(String level) {
					throw new UnsupportedOperationException("Unimplemented method 'convertLevel'");
				}

				@Override
				public AnalyticsWithCalcDTO findAnalyticsByNameLevelDate(String name, String level,
						LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
					throw new UnsupportedOperationException(
							"Unimplemented method 'findAnalyticsByNameLevelDate'");
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
		this.abstractAnalyticHelperService.updateAnalyticsMeanByNameAndLevelAndLevelLot(
				mockDto.name(), mockDto.level(), mockDto.levelLot(), mockDto.mean());
		verify(this.analyticsRepository).updateMeanByNameAndLevelAndLevelLot(mockDto.name(),
				mockDto.level(), mockDto.levelLot(), mockDto.mean());
	}

	@Test
	@DisplayName("Should validate rules when processed by rules validator component")
	void shouldValidateRulesProcessedByRulesValidatorComponent() {
		// Arrange: create sample input records
		List<AnalyticsDTO> records = createSampleRecordList();

		// Act: convert the records to AnalyticsDTO using the validation component
		List<Analytic> analytics = records.stream().map(AnalyticMapper::toNewEntity).toList();

		// Assert: validate the rules generated by the component
		assertEquals(records.stream().map(AnalyticsDTO::rules).toList(),
				analytics.stream().map(Analytic::getRules).toList(),
				"The rules processed by the RulesValidatorComponent should match the input rules");

	}

	@Test
	@DisplayName("Should save records successfully when valid analytics data is provided")
	void saveNewAnalyticsRecords_WithValidRecords_ShouldSaveSuccessfully() {
		List<AnalyticsDTO> records = createSampleRecordList();
		when(this.analyticsRepository.existsByDateAndLevelAndName(any(), any(), any()))
				.thenReturn(false);
		when(this.analyticsRepository.saveAll(any()))
				.thenAnswer(invocation -> invocation.getArgument(0));

		assertDoesNotThrow(
				() -> this.abstractAnalyticHelperService.saveNewAnalyticsRecords(records));
		verify(this.analyticsRepository, times(1)).saveAll(any());
	}

	@Test
	@DisplayName("Should throw exception when trying to save duplicate analytics records")
	void saveNewAnalyticsRecords_WithDuplicateRecords_ShouldThrowException() {
		List<AnalyticsDTO> records = createSampleRecordList();
		when(this.analyticsRepository.existsByDateAndLevelAndName(any(), any(), any()))
				.thenReturn(true);

		assertThrows(CustomGlobalErrorHandling.DataIntegrityViolationException.class,
				() -> this.abstractAnalyticHelperService.saveNewAnalyticsRecords(records));
		verify(this.analyticsRepository, never()).saveAll(any());
	}

	@Test
	@DisplayName("Should return record when searching by valid ID")
	void findById_WithValidId_ShouldReturnRecord() {
		Long id = 1L;
		Analytic analytic = AnalyticMapper.toEntity(createSampleRecord());
		analytic.setId(id);

		when(this.analyticsRepository.findById(id)).thenReturn(Optional.of(analytic));

		AnalyticsDTO result = this.abstractAnalyticHelperService.findOneById(id);

		assertNotNull(result);

		assertEquals(AnalyticMapper.toRecord(analytic), result);
	}

	@Test
	@DisplayName("Should throw exception when searching by invalid ID")
	void findById_WithInvalidId_ShouldThrowException() {
		Long id = 999L;
		when(this.analyticsRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> this.abstractAnalyticHelperService.findOneById(id));
	}

	@Test
	@DisplayName("Should return filtered records when searching by name and level")
	void findAnalyticsByNameAndLevel_WithFilters_ShouldReturnFilteredRecords() {
		String name = "Glucose";
		String level = "Normal";
		Pageable pageable = PageRequest.of(0, 10);
		List<Analytic> expectedRecords = createSampleRecordList().stream()
				.filter(r -> r.name().equals(name) && r.level().equals(level)).toList().stream()
				.map(AnalyticMapper::toNewEntity).toList();

		when(this.analyticsRepository.findByNameAndLevel(pageable, name, level))
				.thenReturn(expectedRecords);

		List<AnalyticsDTO> result = this.abstractAnalyticHelperService
				.findAnalyticsByNameAndLevel(pageable, name, level);

		assertEquals(expectedRecords.size(), result.size());
		verify(this.analyticsRepository).findByNameAndLevel(pageable, name, level);
	}

	@Test
	@DisplayName("Should return filtered records when searching by date range")
	void findAllAnalyticsByNameAndLevelAndDate_WithDateRange_ShouldReturnFilteredRecords() {
		String name = "Glucose";
		String level = "Normal";
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 1, 2, 0, 0);
		List<Analytic> expectedRecords =
				createDateRangeRecords().stream().map(AnalyticMapper::toNewEntity).toList();

		when(this.analyticsRepository.findByNameAndLevelAndDateBetween(eq(name), eq(level),
				eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(expectedRecords);

		List<AnalyticsDTO> result = this.abstractAnalyticHelperService
				.findAnalyticsByNameAndLevelAndDate(name, level, startDate, endDate, null);

		assertNotNull(result);
		assertEquals(expectedRecords.size(), result.size());
	}

	@Test
	@DisplayName("Should delete analytics when valid ID is provided")
	void deleteAnalyticsById_WithValidId_ShouldDelete() {
		Long id = 1L;
		when(this.analyticsRepository.existsById(id)).thenReturn(true);
		doNothing().when(this.analyticsRepository).deleteById(id);

		assertDoesNotThrow(() -> this.abstractAnalyticHelperService.deleteAnalyticsById(id));

		verify(this.analyticsRepository).deleteById(id);
	}

	@Test
	@DisplayName("Should throw exception when deleting analytics with invalid ID")
	void deleteAnalyticsById_WithInvalidId_ShouldThrowException() {
		Long id = 999L;
		when(this.analyticsRepository.existsById(id)).thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> this.abstractAnalyticHelperService.deleteAnalyticsById(id));
		verify(this.analyticsRepository, never()).deleteById(id);
	}

	@Test
	@DisplayName("Should not throw exception when validating existing analytics name")
	void ensureNameExists_WithValidName_ShouldNotThrowException() {
		String name = "Glucose";
		when(this.analyticsRepository.existsByName(name.toUpperCase())).thenReturn(true);

		assertDoesNotThrow(() -> this.abstractAnalyticHelperService.ensureNameExists(name));
	}

	@Test
	@DisplayName("Should throw exception when validating non-existent analytics name")
	void ensureNameExists_WithInvalidName_ShouldThrowException() {
		String name = "NonExistentTest";
		when(this.analyticsRepository.existsByName(name.toUpperCase())).thenReturn(false);

		assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
				() -> this.abstractAnalyticHelperService.ensureNameExists(name));
	}

	@Test
	@DisplayName("Should throw exception when analytics name does not exist")
	void ensureNameNotExists_WithInvalidName_ShouldThrowException() {
		String name = "Glucose";
		when(this.analyticsRepository.existsByName(name.toUpperCase())).thenReturn(false);

		CustomGlobalErrorHandling.ResourceNotFoundException exception =
				assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
						() -> this.abstractAnalyticHelperService.ensureNameExists(name));

		assertEquals("AnalyticsDTO by name not found", exception.getMessage());
	}

	@Test
	@DisplayName("Should return true when checking non-existent analytics record")
	void isAnalyticsNonExistent_WithNonExistentRecord_ShouldReturnTrue() {
		AnalyticsDTO analyticsRecord = createSampleRecord();
		when(this.analyticsRepository.existsByDateAndLevelAndName(analyticsRecord.date(),
				analyticsRecord.level(), analyticsRecord.name())).thenReturn(false);

		boolean result = this.abstractAnalyticHelperService.isAnalyticsNonExistent(analyticsRecord);

		assertTrue(result);
	}

	@Test
	@DisplayName("Should return false when checking existing analytics record")
	void isAnalyticsNonExistent_WithExistentRecord_ShouldReturnFalse() {
		AnalyticsDTO analyticsRecord = createSampleRecord();
		when(this.analyticsRepository.existsByDateAndLevelAndName(analyticsRecord.date(),
				analyticsRecord.level(), analyticsRecord.name())).thenReturn(true);

		boolean result = this.abstractAnalyticHelperService.isAnalyticsNonExistent(analyticsRecord);

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

		List<GroupedValuesByLevelDTO> result = this.abstractAnalyticHelperService
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
		List<Analytic> analytics =
				createSampleRecordList().stream().map(AnalyticMapper::toNewEntity).toList();

		when(this.analyticsRepository.findByNameAndLevelAndDateBetween(eq(name), eq(level),
				eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(analytics);

		// Act
		var result = this.abstractAnalyticHelperService.calculateMeanAndStandardDeviation(name,
				level, startDate, endDate, Pageable.unpaged());

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
		when(this.controlRulesValidators.validateRules(any())).thenReturn("Validation Results");
		doNothing().when(this.emailService).sendFailedAnalyticsNotification(any(), any());

		// Act
		this.abstractAnalyticHelperService.processFailedRecordsNotification(failedRecords);

		// Assert
		verify(this.emailService).sendFailedAnalyticsNotification(eq(failedRecords), any());
		verify(this.controlRulesValidators).validateRules(failedRecords);
	}

	@Test
	@DisplayName("Should handle empty failed records list correctly")
	void processFailedRecordsNotification_WithEmptyList_ShouldNotSendNotification() {
		// Arrange
		List<AnalyticsDTO> emptyList = List.of();

		// Act
		this.abstractAnalyticHelperService.processFailedRecordsNotification(emptyList);

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
					analyticsRecord
							.setRules("Approved according to current Westgard configured rules"); // Invalid
																									// rule
					return AnalyticMapper.toRecord(analyticsRecord);
				}).toList();

		GroupedValuesByLevelDTO groupedRecords =
				new GroupedValuesByLevelDTO("Normal", validRecords);

		// Act
		boolean result = this.abstractAnalyticHelperService.isGroupedRecordValid(groupedRecords);

		// Assert
		assertTrue(result);
	}

	@Test
	@DisplayName("Should identify invalid grouped records correctly")
	void isGroupedRecordValid_WithInvalidRecords_ShouldReturnFalse() {
		// Arrange
		List<AnalyticsDTO> invalidRecords = createSampleRecordList().stream()
				.map((AnalyticMapper::toEntity)).map(analyticsRecord -> {
					analyticsRecord.setRules("-3s"); // Invalid rule
					return AnalyticMapper.toRecord(analyticsRecord);
				}).toList();

		GroupedValuesByLevelDTO groupedRecords =
				new GroupedValuesByLevelDTO("Normal", invalidRecords);

		// Act
		boolean result = this.abstractAnalyticHelperService.isGroupedRecordValid(groupedRecords);

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
		List<Analytic> analytics =
				createSampleRecordList().stream().map(AnalyticMapper::toNewEntity).toList();

		when(this.analyticsRepository.findByNameAndDateBetweenGroupByLevel(eq(name), eq(startDate),
				eq(endDate), any(Pageable.class))).thenReturn(analytics);

		// Act
		var result = this.abstractAnalyticHelperService.calculateGroupedMeanAndStandardDeviation(
				name, startDate, endDate, Pageable.unpaged());

		// Assert
		assertNotNull(result);
		assertFalse(result.isEmpty());
		result.forEach(group -> {
			assertNotNull(group.level());
			assertTrue(group.values().get(0).mean() > 0);
			assertTrue(group.values().get(0).standardDeviation() >= 0);
		});
	}

	@Test
	@DisplayName("Should handle analytics find with date range correctly")
	void findAnalyticsByDate_WithValidDateRange_ShouldReturnCorrectRecords() {
		// Arrange
		LocalDateTime startDate = LocalDateTime.now().minusDays(7);
		LocalDateTime endDate = LocalDateTime.now();
		List<Analytic> expectedAnalytics =
				createDateRangeRecords().stream().map(AnalyticMapper::toNewEntity).toList();

		when(this.analyticsRepository.findByDateBetween(startDate, endDate))
				.thenReturn(expectedAnalytics);

		// Act
		List<AnalyticsDTO> result =
				this.abstractAnalyticHelperService.findAnalyticsByDate(startDate, endDate);

		// Assert
		assertNotNull(result);
		assertEquals(expectedAnalytics.size(), result.size());
		verify(this.analyticsRepository).findByDateBetween(startDate, endDate);
	}
}
