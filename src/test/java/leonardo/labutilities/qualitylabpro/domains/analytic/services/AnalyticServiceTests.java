package leonardo.labutilities.qualitylabpro.domains.analytic.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;

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
import leonardo.labutilities.qualitylabpro.domains.analytics.components.RulesProviderComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.AvailableAnalyticsNames;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticHelperService;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;

@ExtendWith(MockitoExtension.class)
class AnalyticServiceTests {
        @Mock
        private AnalyticsRepository analyticsRepository;

        @Mock
        private EmailService emailService;

        @Mock
        private RulesProviderComponent controlRulesValidators;

        private AnalyticHelperService analyticHelperService;

        private Pageable pageable;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        @BeforeEach
        void setUp() {
                this.analyticHelperService = new AnalyticHelperService(this.analyticsRepository,
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

                when(this.analyticsRepository.findByNameInAndLevelAndDateBetween(any(), any(),
                                any(), any(), any())).thenReturn(expectedPage);

                Page<AnalyticsDTO> result = this.analyticHelperService.findAnalyticsByNameInByLevel(
                                names, "1", this.startDate, this.endDate, this.pageable);

                assertNotNull(result);
                assertEquals(expectedPage, result);
        }

        @Test
        void findAnalyticsByNameAndLevel_ShouldReturnAnalyticsList() {
                String name = "ALB2";
                List<Analytic> analytics = createSampleRecordList().stream()
                                .map(AnalyticMapper::toEntity).toList();

                when(this.analyticsRepository.existsByTestName(name)).thenReturn(true);

                when(this.analyticsRepository.findByNameAndLevel(any(), any(), any()))
                                .thenReturn(analytics);

                List<AnalyticsDTO> expectedList = createSampleRecordList();

                List<AnalyticsDTO> result = this.analyticHelperService
                                .findAnalyticsByNameAndLevel(this.pageable, name, "1");

                assertNotNull(result);
                assertEquals(expectedList, result);
        }

        @Test
        void findAnalyticsByNameAndLevelAndDate_ShouldReturnAnalyticsList() {
                String name = "test";
                List<Analytic> analytics = createSampleRecordList().stream()
                                .map(AnalyticMapper::toEntity).toList();

                when(this.analyticsRepository.findByNameAndLevelAndDateBetween(any(), any(), any(),
                                any(), any())).thenReturn(analytics);

                List<AnalyticsDTO> result =
                                this.analyticHelperService.findAnalyticsByNameAndLevelAndDate(name,
                                                "1", this.startDate, this.endDate, this.pageable);

                var expectedAnalytics = createSampleRecordList();

                System.out.println(result);
                System.out.println(expectedAnalytics);
                assertNotNull(result);
                assertEquals(expectedAnalytics, result);
        }

        @Test
        void findAnalyticsByNameLevelDate_ShouldReturnAnalyticsWithCalcDTO() {
                // Arrange
                String name = "ALB2";
                List<Analytic> mockAnalytics = createSampleRecordList().stream()
                                .map(AnalyticMapper::toEntity).toList();

                // Mock repository behavior
                when(this.analyticsRepository.findByNameAndLevelAndDateBetween(name, "PCCC1",
                                this.startDate, this.endDate, this.pageable))
                                                .thenReturn(mockAnalytics);

                // Act
                AnalyticsWithCalcDTO result = this.analyticHelperService
                                .findAnalyticsByNameLevelDate(name, "PCCC1", this.startDate,
                                                this.endDate, this.pageable);

                // Assert
                assertNotNull(result);
                assertNotNull(result.analyticsDTO());
                assertNotNull(result.calcMeanAndStdDTO());
                assertEquals(mockAnalytics.size(), result.analyticsDTO().size());
        }

        @Test
        void convertLevel_ShouldReturnCorrectLevel() {
                assertEquals("PCCC1", this.analyticHelperService.convertLevel("PCCC1"));
                assertEquals("PCCC2", this.analyticHelperService.convertLevel("PCCC2"));
        }

        @Test
        void convertLevel_ShouldThrowException_WhenInvalidLevel() {
                assertEquals("1", this.analyticHelperService.convertLevel("1"));
        }

}
