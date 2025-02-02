package leonardo.labutilities.qualitylabpro.services.analytics;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.components.ControlRulesValidators;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public abstract class AbstractAnalyticService extends AbstractAnalyticHelperService {

	public AbstractAnalyticService(AnalyticsRepository analyticsRepository,
			EmailService emailService, ControlRulesValidators controlRulesValidators) {
		super(analyticsRepository, emailService, controlRulesValidators);
	}

	@Override
	public abstract List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable, String name,
			String level);

	@Override
	public abstract List<AnalyticsDTO> findAnalyticsByNameAndLevelAndDate(String name, String level,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

	public abstract String convertLevel(String level);
}
