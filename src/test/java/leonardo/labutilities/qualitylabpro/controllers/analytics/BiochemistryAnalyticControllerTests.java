package leonardo.labutilities.qualitylabpro.controllers.analytics;

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
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.MeanAndStdDeviationDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.UpdateAnalyticsMeanDTO;
import leonardo.labutilities.qualitylabpro.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.services.analytics.BiochemistryAnalyticService;
import leonardo.labutilities.qualitylabpro.services.authentication.TokenService;

@WebMvcTest(BiochemistryAnalyticsController.class)
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
	private BiochemistryAnalyticService biochemistryAnalyticsService;

	@Autowired
	private JacksonTester<List<AnalyticsDTO>> jacksonGenericValuesRecord;

	@Autowired
	private JacksonTester<UpdateAnalyticsMeanDTO> jacksonUpdateAnalyticsMeanRecord;

	@Test
	@DisplayName("Should return analytics list when searching by level")
	void getAllAnalytics_by_level_return_list() throws Exception {
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
	void analytics_post_return_201() throws Exception {
		List<AnalyticsDTO> records = createSampleRecordList();
		this.mockMvc
				.perform(post("/biochemistry-analytics").contentType(MediaType.APPLICATION_JSON)
						.content(this.jacksonGenericValuesRecord.write(records).getJson()))
				.andExpect(status().isCreated());
		verify(this.biochemistryAnalyticsService, times(1)).saveNewAnalyticsRecords(anyList());
	}

	@Test
	@DisplayName("Should return no content when updating analytics records")
	void analytics_put_return_204() throws Exception {
		var mockDto = new UpdateAnalyticsMeanDTO("Glucose", "PCCC1", "1234", 10.5);
		this.mockMvc
				.perform(patch("/biochemistry-analytics").contentType(MediaType.APPLICATION_JSON)
						.content(this.jacksonUpdateAnalyticsMeanRecord.write(mockDto).getJson()))
				.andExpect(status().isNoContent());
		verify(this.biochemistryAnalyticsService, times(1))
				.updateAnalyticsMeanByNameAndLevelAndLevelLot("Glucose", "PCCC1", "1234", 10.5);
	}

	@Test
	@DisplayName("Should return paginated analytics list when requesting all analytics")
	void getAllAnalytics_return_list() throws Exception {
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
	void getAnalyticsByDateRange_return_analytics() throws Exception {
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
	void getMeanAndStandardDeviation_return_result() throws Exception {
		MeanAndStdDeviationDTO result = new MeanAndStdDeviationDTO(10.5, 2.3);
		LocalDateTime startDate = LocalDateTime.parse("2025-01-01 00:00:00",
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime endDate = LocalDateTime.parse("2025-01-05 00:00:00",
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		when(this.biochemistryAnalyticsService.calculateMeanAndStandardDeviation(eq("Hemoglobin"),
				eq("High"), eq(startDate), eq(endDate), any(Pageable.class))).thenReturn(result);

		this.mockMvc.perform(get("/biochemistry-analytics/mean-standard-deviation")
				.param("name", "Hemoglobin").param("level", "High")
				.param("startDate", "2025-01-01 00:00:00").param("endDate", "2025-01-05 00:00:00")
				.param("page", "0").param("size", "10")).andExpect(status().isOk());

		verify(this.biochemistryAnalyticsService).calculateMeanAndStandardDeviation(
				eq("Hemoglobin"), eq("High"), eq(startDate), eq(endDate), any(Pageable.class));
	}
}
