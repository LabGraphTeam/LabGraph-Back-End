package leonardo.labutilities.qualitylabpro.domains.analytic.controllers;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import leonardo.labutilities.qualitylabpro.configs.TestSecurityConfig;
import leonardo.labutilities.qualitylabpro.domains.analytics.controllers.HematologyAnalyticsController;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.HematologyAnalyticService;
import leonardo.labutilities.qualitylabpro.domains.shared.authentication.TokenService;
import leonardo.labutilities.qualitylabpro.domains.users.repositories.UserRepository;

@WebMvcTest(HematologyAnalyticsController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
class HematologyAnalyticControllerTests {

	@MockitoBean
	private TokenService tokenService;

	@MockitoBean
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HematologyAnalyticService hematologyAnalyticsService;

	@Autowired
	private JacksonTester<List<AnalyticsDTO>> jacksonGenericValuesRecord;

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

		when(this.hematologyAnalyticsService.findAnalyticsByNameInByLevel(anyList(), any(), any(),
				any(), any(Pageable.class))).thenReturn(page);

		this.mockMvc.perform(get("/hematology-analytics/level-date-range").param("level", "PCCC1")
				.param("startDate", "2025-01-01 00:00:00").param("endDate", "2025-01-05 00:00:00"))
				.andExpect(status().isOk());

		verify(this.hematologyAnalyticsService, times(1)).findAnalyticsByNameInByLevel(anyList(),
				any(), any(), any(), any(Pageable.class));
	}

	@Test
	@DisplayName("Should return created status when saving valid analytics records")
	void shouldReturnCreatedStatusWhenSavingAnalyticsRecords() throws Exception {
		List<AnalyticsDTO> records = createSampleRecordList();
		this.mockMvc
				.perform(post("/hematology-analytics").contentType(MediaType.APPLICATION_JSON)
						.content(this.jacksonGenericValuesRecord.write(records).getJson()))
				.andExpect(status().isCreated());
		verify(this.hematologyAnalyticsService, times(1)).saveNewAnalyticsRecords(anyList());
	}

	@Test
	@DisplayName("Should return paginated analytics list when requesting all analytics")
	void shouldReturnPaginatedAnalyticsListWhenRequestingAllAnalytics() throws Exception {
		List<AnalyticsDTO> records = createSampleRecordList();
		Page<AnalyticsDTO> page = new PageImpl<>(records);

		when(this.hematologyAnalyticsService.findAnalyticsPagedByNameIn(anyList(),
				any(Pageable.class))).thenReturn(page);

		this.mockMvc.perform(get("/hematology-analytics").param("page", "0").param("size", "10"))
				.andExpect(status().isOk());

		verify(this.hematologyAnalyticsService, times(1)).findAnalyticsPagedByNameIn(anyList(),
				any(Pageable.class));
	}

	@Test
	@DisplayName("Should return analytics records when searching within date range")
	void shouldReturnAnalyticsRecordsWhenSearchingWithinDateRange() throws Exception {
		List<AnalyticsDTO> records = createSampleRecordList();
		Page<AnalyticsDTO> page = new PageImpl<>(records);

		when(this.hematologyAnalyticsService.findAnalyticsByNameInAndDateBetween(anyList(), any(),
				any(), any())).thenReturn(page);

		this.mockMvc.perform(get("/hematology-analytics/date-range")
				.param("startDate", "2025-01-01 00:00:00").param("endDate", "2025-01-05 00:00:00"))
				.andExpect(status().isOk());

		verify(this.hematologyAnalyticsService, times(1))
				.findAnalyticsByNameInAndDateBetween(anyList(), any(), any(), any());
	}

	@Test
	@DisplayName("Should return mean and standard deviation when searching within date range")
	void shouldReturnMeanAndStandardDeviationWhenSearchingWithinDateRange() throws Exception {
		MeanAndStdDeviationDTO result = new MeanAndStdDeviationDTO(10.5, 2.3);
		LocalDateTime startDate = this.parse("2025-01-01 00:00:00");
		LocalDateTime endDate = this.parse("2025-01-05 00:00:00");

		when(this.hematologyAnalyticsService.calculateMeanAndStandardDeviation(eq("Hemoglobin"),
				eq("High"), eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(result);

		this.mockMvc.perform(get("/hematology-analytics/mean-standard-deviation")
				.param("name", "Hemoglobin").param("level", "High")
				.param("startDate", "2025-01-01 00:00:00").param("endDate", "2025-01-05 00:00:00")
				.param("page", "0").param("size", "10")).andExpect(status().isOk());

		verify(this.hematologyAnalyticsService).calculateMeanAndStandardDeviation(eq("Hemoglobin"),
				eq("High"), eq(startDate), eq(endDate), any(Pageable.class));
	}

	@Test
	void shouldReturnGroupedAnalyticsByLevel() throws Exception {
		String name = "Hemoglobin";
		String level = "High";
		LocalDateTime startDate = this.parse("2025-01-01 00:00:00");
		LocalDateTime endDate = this.parse("2025-01-05 00:00:00");
		List<GroupedResultsByLevelDTO> mockedResult = List.of(this.buildGroupedResults(level));

		when(this.hematologyAnalyticsService.findAnalyticsWithGroupedResults(eq(name),
				eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(mockedResult);

		this.mockMvc.perform(get("/hematology-analytics/grouped-by-level").param("name", name)
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

		when(this.hematologyAnalyticsService.calculateGroupedMeanAndStandardDeviation(eq(name),
				eq(startDate), eq(endDate), any(Pageable.class)))
						.thenReturn(groupedValuesByLevelDTOList);

		this.mockMvc.perform(get("/hematology-analytics/grouped-by-level/mean-deviation")
				.param("name", name).param("startDate", "2025-01-01 00:00:00")
				.param("endDate", "2025-01-05 00:00:00").param("page", "0").param("size", "10"))
				.andExpect(status().isOk());
	}

	// V2
	@Test
	@DisplayName("Should return analytics with calculations when searching by name, level and date range (V2)")
	void shouldReturnAnalyticsWithCalculationsWhenSearchingByNameLevelAndDateRangeV2()
			throws Exception {
		String name = "Hemoglobin";
		String level = "High";
		LocalDateTime startDate = this.parse("2025-01-01 00:00:00");
		LocalDateTime endDate = this.parse("2025-01-05 00:00:00");
		List<AnalyticsDTO> records = createSampleRecordList();
		MeanAndStdDeviationDTO result = new MeanAndStdDeviationDTO(10.5, 2.3);

		AnalyticsWithCalcDTO dummyResult = new AnalyticsWithCalcDTO(records, result);

		when(this.hematologyAnalyticsService.findAnalyticsByNameLevelDate(eq(name), eq(level),
				eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(dummyResult);

		this.mockMvc.perform(get("/hematology-analytics/name-and-level-date-range")
				.param("name", name).param("level", level).param("startDate", "2025-01-01 00:00:00")
				.param("endDate", "2025-01-05 00:00:00").param("page", "0").param("size", "10"))
				.andExpect(status().isOk());

		verify(this.hematologyAnalyticsService).findAnalyticsByNameLevelDate(eq(name), eq(level),
				eq(startDate), eq(endDate), any(Pageable.class));
	}

}
