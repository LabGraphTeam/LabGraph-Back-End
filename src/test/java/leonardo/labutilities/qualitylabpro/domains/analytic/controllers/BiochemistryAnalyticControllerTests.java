package leonardo.labutilities.qualitylabpro.domains.analytic.controllers;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import leonardo.labutilities.qualitylabpro.configs.TestSecurityConfig;
import leonardo.labutilities.qualitylabpro.domains.analytics.controllers.BiochemistryAnalyticController;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.UpdateAnalyticsMeanDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticStatisticsService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.BiochemistryAnalyticService;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.TokenService;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;
import leonardo.labutilities.qualitylabpro.domains.users.repositories.UserRepository;

@WebMvcTest(BiochemistryAnalyticController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
class BiochemistryAnalyticControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TokenService tokenService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private AnalyticsRepository analyticsRepository;

	@MockitoBean
	private BiochemistryAnalyticService biochemistryAnalyticsService;

	@MockitoBean
	private AnalyticStatisticsService analyticsStatisticsService;

	@Autowired
	private JacksonTester<List<AnalyticsDTO>> jacksonGenericValuesRecord;

	@Autowired
	private JacksonTester<UpdateAnalyticsMeanDTO> jacksonUpdateAnalyticsMeanRecord;

	private static final DateTimeFormatter FORMATTER =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private LocalDateTime parse(String dateStr) {
		return LocalDateTime.parse(dateStr, FORMATTER);
	}

	private GroupedResultsByLevelDTO buildGroupedResults(String level) {
		var records = createSampleRecordList();
		var meanDTO = new MeanAndStdDeviationDTO(10.5, 2.3);
		var groupedValues = new GroupedValuesByLevelDTO(level, records);
		var groupedMean = new GroupedMeanAndStdByLevelDTO(level, List.of(meanDTO));
		return new GroupedResultsByLevelDTO(groupedValues, groupedMean);
	}

	@Test
	@DisplayName("Should return analytics list when searching by level")
	void shouldReturnAnalyticsListWhenSearchingByLevel() throws Exception {
		List<AnalyticsDTO> records = createSampleRecordList();
		Page<AnalyticsDTO> page = new PageImpl<>(records);

		when(this.biochemistryAnalyticsService.findAnalyticsByNameInByLevel(anyList(), any(), any(),
				any(), any(Pageable.class))).thenReturn(page);

		this.mockMvc.perform(get("/biochemistry-analytics/level-date-range").param("level", "PCCC1")
				.param("startDate", "2025-01-01 00:00:00").param("endDate", "2025-01-05 00:00:00"))
				.andExpect(status().isOk());

		verify(this.biochemistryAnalyticsService, times(1)).findAnalyticsByNameInByLevel(anyList(),
				any(), any(), any(), any(Pageable.class));
	}

	@Test
	@DisplayName("Should return created status when saving valid analytics records")
	void shouldReturnCreatedStatusWhenSavingAnalyticsRecords() throws Exception {
		List<AnalyticsDTO> records = createSampleRecordList();
		this.mockMvc
				.perform(post("/biochemistry-analytics").contentType(MediaType.APPLICATION_JSON)
						.content(this.jacksonGenericValuesRecord.write(records).getJson()))
				.andExpect(status().isCreated());
		verify(this.biochemistryAnalyticsService, times(1)).saveNewAnalyticsRecords(anyList());
	}

	@Test
	@DisplayName("Should return no content when updating analytics records")
	void shouldReturnNoContentWhenUpdatingAnalyticsRecords() throws Exception {
		var mockDto = new UpdateAnalyticsMeanDTO("Glucose", "PCCC1", "1234", 10.5);

		when(biochemistryAnalyticsService.convertLevel("PCCC1")).thenReturn("PCCC1");
		this.mockMvc
				.perform(patch("/biochemistry-analytics").contentType(MediaType.APPLICATION_JSON)
						.content(this.jacksonUpdateAnalyticsMeanRecord.write(mockDto).getJson()))
				.andExpect(status().isNoContent());

		verify(this.biochemistryAnalyticsService, times(1))
				.updateAnalyticsMeanByNameAndLevelAndLevelLot("Glucose", "PCCC1", "1234", 10.5);
	}

	@Test
	@DisplayName("Should return paginated analytics list when requesting all analytics")
	void shouldReturnPaginatedAnalyticsListWhenRequestingAllAnalytics() throws Exception {
		List<AnalyticsDTO> records = createSampleRecordList();
		Page<AnalyticsDTO> page = new PageImpl<>(records);

		when(this.biochemistryAnalyticsService.findAnalyticsPagedByNameIn(anyList(),
				any(Pageable.class))).thenReturn(page);

		this.mockMvc.perform(get("/biochemistry-analytics").param("page", "0").param("size", "10"))
				.andExpect(status().isOk());

		verify(this.biochemistryAnalyticsService, times(1)).findAnalyticsPagedByNameIn(anyList(),
				any(Pageable.class));
	}

	@Test
	@DisplayName("Should return analytics records when searching within date range")
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void shouldReturnAnalyticsRecordsWhenSearchingWithinDateRange() throws Exception {
		Page<AnalyticsDTO> records = new PageImpl<>(createSampleRecordList());

		when(this.biochemistryAnalyticsService.findAnalyticsByNameInAndDateBetween(anyList(), any(),
				any(), any())).thenReturn(records);

		this.mockMvc.perform(get("/biochemistry-analytics/date-range")
				.param("startDate", "2025-01-01 00:00:00").param("endDate", "2025-01-05 00:00:00"))
				.andExpect(status().isOk());

		verify(this.biochemistryAnalyticsService, times(1))
				.findAnalyticsByNameInAndDateBetween(anyList(), any(), any(), any());
	}

	@Test
	@DisplayName("Should return mean and standard deviation when searching within date range")
	void shouldReturnMeanAndStandardDeviationWhenSearchingWithinDateRange() throws Exception {
		MeanAndStdDeviationDTO result = new MeanAndStdDeviationDTO(10.5, 2.3);
		LocalDateTime startDate = this.parse("2025-01-01 00:00:00");
		LocalDateTime endDate = this.parse("2025-01-05 00:00:00");

		var mockList = createSampleRecordList().stream().map(AnalyticMapper::toEntity).toList();

		when(analyticsRepository.findByNameAndLevelAndDateBetween(eq("ALB2"), eq("PCCC1"),
				eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(mockList);

		when(this.analyticsStatisticsService.calculateMeanAndStandardDeviation(eq("ALB2"),
				eq("PCCC1"), eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(result);

		when(biochemistryAnalyticsService.convertLevel("1")).thenReturn("PCCC1");

		this.mockMvc.perform(get("/biochemistry-analytics/mean-standard-deviation")
				.param("name", "ALB2").param("level", "1").param("startDate", "2025-01-01 00:00:00")
				.param("endDate", "2025-01-05 00:00:00").param("page", "0").param("size", "10"))
				.andExpect(status().isOk());

		verify(this.analyticsStatisticsService).calculateMeanAndStandardDeviation(eq("ALB2"),
				eq("PCCC1"), eq(startDate), eq(endDate), any(Pageable.class));
	}

	@Test
	void shouldReturnGroupedAnalyticsByLevel() throws Exception {
		String name = "Hemoglobin";
		String level = "High";
		LocalDateTime startDate = this.parse("2025-01-01 00:00:00");
		LocalDateTime endDate = this.parse("2025-01-05 00:00:00");
		List<GroupedResultsByLevelDTO> mockedResult = List.of(this.buildGroupedResults(level));

		when(this.biochemistryAnalyticsService.findAnalyticsWithGroupedResults(eq(name),
				eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(mockedResult);

		this.mockMvc.perform(get("/biochemistry-analytics/grouped-by-level").param("name", name)
				.param("startDate", "2025-01-01 00:00:00").param("endDate", "2025-01-05 00:00:00")
				.param("page", "0").param("size", "10")).andExpect(status().isOk());
	}

	@Test
	void shouldReturnGroupedMeanAndDeviationByLevel() throws Exception {
		String name = "Hemoglobin";
		String level = "High";
		LocalDateTime startDate = this.parse("2025-01-01 00:00:00");
		LocalDateTime endDate = this.parse("2025-01-05 00:00:00");

		MeanAndStdDeviationDTO meanAndStdDeviationDTO = new MeanAndStdDeviationDTO(10.5, 2.3);
		List<MeanAndStdDeviationDTO> values = List.of(meanAndStdDeviationDTO);
		var groupedValuesByLevelDTOList = List.of(new GroupedMeanAndStdByLevelDTO(level, values));

		when(this.analyticsStatisticsService.calculateGroupedMeanAndStandardDeviation(eq(name),
				eq(startDate), eq(endDate), any(Pageable.class)))
						.thenReturn(groupedValuesByLevelDTOList);

		this.mockMvc.perform(get("/biochemistry-analytics/grouped-by-level/mean-deviation")
				.param("name", name).param("startDate", "2025-01-01 00:00:00")
				.param("endDate", "2025-01-05 00:00:00").param("page", "0").param("size", "10"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Should return analytics with calculations when searching by name, level and date range")
	void shouldReturnAnalyticsWithCalculationsWhenSearchingByNameLevelAndDateRange()
			throws Exception {
		String name = "ALB2";
		String level = "PCCC1";
		String startDateStr = "2025-01-01 00:00:00";
		String endDateStr = "2025-01-05 00:00:00";
		LocalDateTime startDate = this.parse(startDateStr);
		LocalDateTime endDate = this.parse(endDateStr);

		AnalyticsWithCalcDTO dummyResult = new AnalyticsWithCalcDTO(createSampleRecordList(),
				new MeanAndStdDeviationDTO(10.5, 2.3));

		when(biochemistryAnalyticsService.convertLevel(level)).thenReturn(level);

		when(biochemistryAnalyticsService.findAnalyticsByNameLevelDate(eq(name), eq(level),
				eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(dummyResult);

		mockMvc.perform(get("/biochemistry-analytics/name-and-level-date-range").param("name", name)
				.param("level", level).param("startDate", startDateStr).param("endDate", endDateStr)
				.param("page", "0").param("size", "10")).andExpect(status().isOk());

		verify(biochemistryAnalyticsService).findAnalyticsByNameLevelDate(eq(name), eq(level),
				eq(startDate), eq(endDate), any(Pageable.class));
	}

}
