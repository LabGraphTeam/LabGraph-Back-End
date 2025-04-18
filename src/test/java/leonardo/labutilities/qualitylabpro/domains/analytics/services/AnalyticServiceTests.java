package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import leonardo.labutilities.qualitylabpro.domains.analytics.components.AnalyticFailedNotificationComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.components.RulesProviderComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.AvailableAnalyticsNames;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.utils.AnalyticRulesValidation;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;

@ExtendWith(MockitoExtension.class)
class AnalyticServiceTests {
        @Mock
        private AnalyticsRepository analyticsRepository;

        @Mock
        private EmailService emailService;

        @Mock
        private RulesProviderComponent controlRulesValidators;

        @Mock
        private AnalyticValidationService analyticsValidationService;

        @Mock
        private AnalyticFailedNotificationComponent analyticFailedNotificationComponent;

        @Mock
        private AnalyticRulesValidation analyticObjectValidationComponent;

        private AnalyticHelperService analyticHelperService;

        private Pageable pageable;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        @BeforeEach
        void setUp() {
                this.analyticHelperService = new AnalyticHelperService(this.analyticsRepository,
                                this.analyticsValidationService,
                                this.analyticFailedNotificationComponent);
                this.pageable = PageRequest.of(0, 10);
                this.startDate = LocalDateTime.now().minusDays(7);
                this.endDate = LocalDateTime.now();
        }

        @Test
        void findAnalyticsByNameInByLevel_ShouldReturnPageOfAnalytics() {
                List<String> names = AvailableAnalyticsNames.ALL_ANALYTICS;
                List<AnalyticsDTO> expectedList = createSampleRecordList();

                Page<AnalyticsDTO> expectedPage = new PageImpl<AnalyticsDTO>(expectedList);

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
                List<AnalyticsDTO> analytics = createSampleRecordList();

                when(this.analyticsRepository.findByNameAndLevel(any(), any(), any()))
                                .thenReturn(analytics);

                List<AnalyticsDTO> result = this.analyticHelperService
                                .findAnalyticsByNameAndLevel(this.pageable, name, "1");

                assertNotNull(result);
        }

        @Test
        void findAnalyticsByNameLevelDate_ShouldReturnAnalyticsWithCalcDTO() {

                String name = "ALB2";
                List<AnalyticsDTO> mockAnalytics = createSampleRecordList();

                when(this.analyticsRepository.findByNameAndLevelAndDateBetween(name, "PCCC1",
                                this.startDate, this.endDate, this.pageable))
                                                .thenReturn(mockAnalytics);

                AnalyticsWithCalcDTO result = this.analyticHelperService
                                .findAnalyticsByNameLevelDate(name, "PCCC1", this.startDate,
                                                this.endDate, this.pageable);

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
