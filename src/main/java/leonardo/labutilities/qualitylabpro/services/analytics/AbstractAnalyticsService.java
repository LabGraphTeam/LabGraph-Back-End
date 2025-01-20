package leonardo.labutilities.qualitylabpro.services.analytics;

import java.time.LocalDateTime;
import java.util.List;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;

import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import org.springframework.data.domain.Pageable;

public abstract class AbstractAnalyticsService extends AnalyticsHelperService {

	public AbstractAnalyticsService(AnalyticsRepository analyticsRepository, EmailService emailService) {
		super(analyticsRepository, emailService);
	}

	@Override
	public abstract List<AnalyticsRecord> findAnalyticsByNameAndLevel(Pageable pageable,
																	  String name, String level);

	@Override
	public abstract List<AnalyticsRecord>
	findAnalyticsByNameAndLevelAndDate(String name,
									   String level, LocalDateTime dateStart, LocalDateTime dateEnd);

	public abstract String convertLevel(String level);
}
