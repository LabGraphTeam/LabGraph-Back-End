package leonardo.labutilities.qualitylabpro.services.analytics;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.components.ControlRulesValidators;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HematologyAnalyticService extends AbstractAnalyticService {

    public HematologyAnalyticService(AnalyticsRepository analyticsRepository, EmailService emailService, ControlRulesValidators controlRulesValidators) {
        super(analyticsRepository, emailService, controlRulesValidators);
    }

    public Page<AnalyticsDTO> findAnalyticsByNameInByLevel(List<String> names, String level,
                                                           LocalDateTime startDate, LocalDateTime endDate,
                                                           Pageable pageable) {
        return this.findAnalyticsByNameInByLevelBaseMethod(names, this.convertLevel(level),
                                                           startDate, endDate, pageable);
    }

    @Override
    public List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable, String name,
                                                          String level) {
        ensureNameExists(name);
        return findAnalyticsByNameAndLevelWithPagination(pageable, name, convertLevel(level));
    }

    @Override
    public List<AnalyticsDTO> findAnalyticsByNameAndLevelAndDate(String name,
                                                                 String level, LocalDateTime dateStart,
                                                                 LocalDateTime dateEnd) {
        ensureNameExists(name);
        return findAnalyticsByNameLevelAndDate(name, convertLevel(level), dateStart, dateEnd);
    }

    @Override
    public String convertLevel(String inputLevel) {
        return switch (inputLevel) {
            case "1" -> "low";
            case "2" -> "normal";
            case "3" -> "high";
            default -> throw new CustomGlobalErrorHandling.ResourceNotFoundException(
                    "Level not found.");
        };
    }
}
