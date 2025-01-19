package leonardo.labutilities.qualitylabpro.controllers;

import leonardo.labutilities.qualitylabpro.configs.TestSecurityConfig;
import leonardo.labutilities.qualitylabpro.controllers.analytics.HematologyAnalyticsController;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;
import leonardo.labutilities.qualitylabpro.dtos.analytics.MeanAndStdDeviationRecord;
import leonardo.labutilities.qualitylabpro.repositories.UserRepository;
import leonardo.labutilities.qualitylabpro.services.analytics.HematologyAnalyticsService;
import leonardo.labutilities.qualitylabpro.services.authentication.TokenService;
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

import java.util.List;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HematologyAnalyticsController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
public class HematologyAnalyticsControllerTest {

	@MockitoBean
	private TokenService tokenService;

	@MockitoBean
	private UserRepository userRepository;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HematologyAnalyticsService hematologyAnalyticsService;

	@Autowired
	private JacksonTester<List<AnalyticsRecord>> jacksonGenericValuesRecord;

	@Test
	@DisplayName("It should return a list of all analytics by level")
	void getAllAnalytics_by_level_return_list() throws Exception {
		List<AnalyticsRecord> records = createSampleRecordList();

		when(hematologyAnalyticsService
				.findAnalyticsByNameInByLevel(anyList(),any(),any(),any(), any(Pageable.class)))
				.thenReturn(records);

		mockMvc.perform(get("/hematology-analytics/level-date-range")
						.param("level", "PCCC1")
						.param("startDate", "2025-01-01 00:00:00")
						.param("endDate", "2025-01-05 00:00:00"))
				.andExpect(status().isOk());

		verify(hematologyAnalyticsService, times(1))
				.findAnalyticsByNameInByLevel(anyList(),any(), any(),any(), any(Pageable.class));
	}

	@Test
	@DisplayName("It should return HTTP code 201 when analytics records are saved")
	void analytics_post_return_201() throws Exception {
		List<AnalyticsRecord> records = createSampleRecordList();
		mockMvc.perform(post("/hematology-analytics").contentType(MediaType.APPLICATION_JSON)
				.content(jacksonGenericValuesRecord.write(records).getJson()))
				.andExpect(status().isCreated());
		verify(hematologyAnalyticsService, times(1)).saveNewAnalyticsRecords(anyList());
	}

	@Test
	@DisplayName("It should return a list of all analytics with pagination")
	void getAllAnalytics_return_list() throws Exception {
		List<AnalyticsRecord> records = createSampleRecordList();
		Page<AnalyticsRecord> page = new PageImpl<>(records);

		when(hematologyAnalyticsService.findAnalyticsPagedByNameIn(anyList(), any(Pageable.class)))
				.thenReturn(page);

		mockMvc.perform(get("/hematology-analytics")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk());

		verify(hematologyAnalyticsService, times(1))
				.findAnalyticsPagedByNameIn(anyList(), any(Pageable.class));
	}

//	@Test
//	@DisplayName("It should return analytics records by level and name")
//	void getAnalyticsByLevel_return_analytics() throws Exception {
//		List<AnalyticsRecord> records = createSampleRecordList();
//		when(hematologyAnalyticsService.findAnalyticsByNameAndLevel(any(), any(), any()))
//				.thenReturn(records);
//
//		mockMvc.perform(get("/hematology-analytics/name-and-level").param("name", "Hemoglobin")
//				.param("level", "High").param("page", "0").param("size", "10"))
//				.andExpect(status().isOk());
//
//		verify(hematologyAnalyticsService, times(1)).findAnalyticsByNameAndLevel(any(), any(),
//				any());
//	}

	@Test
	@DisplayName("It should return analytics records for a date range")
	void getAnalyticsByDateRange_return_analytics() throws Exception {
		List<AnalyticsRecord> records = createSampleRecordList();
		Page<AnalyticsRecord> page = new PageImpl<>(records);

		when(hematologyAnalyticsService.findAnalyticsByNameInAndDateBetweenWithLinks(anyList(), any(), any(), any()))
				.thenReturn(page);

		mockMvc.perform(get("/hematology-analytics/date-range")
				.param("startDate", "2025-01-01 00:00:00").param("endDate", "2025-01-05 00:00:00"))
				.andExpect(status().isOk());

		verify(hematologyAnalyticsService, times(1))
				.findAnalyticsByNameInAndDateBetweenWithLinks(anyList(), any(), any(), any());
	}

	@Test
	@DisplayName("It should return mean and standard deviation for a date range")
	void getMeanAndStandardDeviation_return_result() throws Exception {
		MeanAndStdDeviationRecord result = new MeanAndStdDeviationRecord(10.5, 2.3);
		when(hematologyAnalyticsService.calculateMeanAndStandardDeviation(any(), any(), any(),
				any())).thenReturn(result);

		mockMvc.perform(get("/hematology-analytics/mean-standard-deviation")
				.param("name", "Hemoglobin").param("level", "High")
				.param("startDate", "2025-01-01 00:00:00").param("endDate",
								"2025-01-05 00:00:00"))
				.andExpect(status().isOk());
		verify(hematologyAnalyticsService, times(1))
				.calculateMeanAndStandardDeviation(any(), any(),
				any(), any());
	}
}
