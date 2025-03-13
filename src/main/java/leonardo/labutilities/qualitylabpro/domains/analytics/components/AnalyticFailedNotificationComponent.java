package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import java.util.List;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.email.EmailService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AnalyticFailedNotificationComponent {
    private final EmailService emailService;
    private final RulesProviderComponent controlRulesValidators;

    public AnalyticFailedNotificationComponent(EmailService emailService,
            RulesProviderComponent controlRulesValidators) {
        this.emailService = emailService;
        this.controlRulesValidators = controlRulesValidators;

    }

    @Async
    public void processFailedRecordsNotification(List<AnalyticsDTO> failedRecords) {
        if (!failedRecords.isEmpty()) {
            try {
                final String content = this.controlRulesValidators.validateRules(failedRecords);

                this.emailService.sendFailedAnalyticsNotification(failedRecords, content);
            } catch (Exception e) {
                log.error("Error sending identifier notification: {}", e);
            }
        }
    }

}
