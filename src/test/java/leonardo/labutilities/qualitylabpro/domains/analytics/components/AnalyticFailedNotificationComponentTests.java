package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticFailedNotificationComponentTests {

    @Mock
    private EmailService emailService;

    @Mock
    private RulesProviderComponent rulesProviderComponent;

    private AnalyticFailedNotificationComponent analyticsFailedNotificationComponent;

    @BeforeEach
    void setUp() {
        analyticsFailedNotificationComponent =
                new AnalyticFailedNotificationComponent(emailService, rulesProviderComponent);
    }

    @Test
    void processFailedRecordsNotification_WithRecords_ShouldSendNotification() {

        // Arrange
        List<AnalyticsDTO> failedRecords = AnalyticsHelperMocks.createSampleRecordList();
        String validationContent = "Validation report content";
        when(rulesProviderComponent.validateRules(failedRecords)).thenReturn(validationContent);

        // Act
        analyticsFailedNotificationComponent.processFailedRecordsNotification(failedRecords);

        // Assert
        verify(rulesProviderComponent, times(1)).validateRules(failedRecords);
        verify(emailService, times(1)).sendFailedAnalyticsNotification(failedRecords,
                validationContent);
    }

    @Test
    void processFailedRecordsNotification_WithEmptyList_ShouldNotSendNotification() {
        // Arrange
        List<AnalyticsDTO> emptyList = Collections.emptyList();

        // Act
        analyticsFailedNotificationComponent.processFailedRecordsNotification(emptyList);

        // Assert
        verify(rulesProviderComponent, never()).validateRules(any());
        verify(emailService, never()).sendFailedAnalyticsNotification(any(), any());
    }

    @Test
    void processFailedRecordsNotification_WhenExceptionOccurs_ShouldHandleGracefully() {
        // Arrange
        List<AnalyticsDTO> failedRecords = AnalyticsHelperMocks.createSampleRecordList();
        when(rulesProviderComponent.validateRules(failedRecords))
                .thenThrow(new RuntimeException("Test exception"));

        // Act
        analyticsFailedNotificationComponent.processFailedRecordsNotification(failedRecords);

        // Assert
        verify(rulesProviderComponent, times(1)).validateRules(failedRecords);
        verify(emailService, never()).sendFailedAnalyticsNotification(any(), any());
    }
}
