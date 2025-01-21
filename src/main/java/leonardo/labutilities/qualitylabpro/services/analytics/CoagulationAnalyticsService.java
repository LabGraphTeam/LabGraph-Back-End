package leonardo.labutilities.qualitylabpro.services.analytics;

import java.time.LocalDateTime;
import java.util.List;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CoagulationAnalyticsService extends AbstractAnalyticsService {

	public CoagulationAnalyticsService(AnalyticsRepository analyticsRepository,
			EmailService emailService) {
		super(analyticsRepository, emailService);
	}

	public Page<AnalyticsRecord> findAnalyticsByNameInByLevel(List<String> names, String level,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		return this.findAnalyticsByNameInByLevelBaseMethod(names, this.convertLevel(level),
				startDate, endDate, pageable);
	}

	@Override
	public List<AnalyticsRecord> findAnalyticsByNameAndLevel(Pageable pageable, String name,
			String level) {
		this.ensureNameExists(name);
		return this.findAnalyticsByNameAndLevelWithPagination(pageable, name,
				this.convertLevel(level));
	}

	@Override
	public List<AnalyticsRecord> findAnalyticsByNameAndLevelAndDate(String name, String level,
			LocalDateTime dateStart, LocalDateTime dateEnd) {
		return this.findAnalyticsByNameLevelAndDate(name.toUpperCase(), this.convertLevel(level),
				dateStart, dateEnd);
	}

	@Override
	public String convertLevel(String inputLevel) {
		return switch (inputLevel) {
			case "1" -> "Normal C. Assayed";
			case "2" -> "Low Abn C. Assayed";
			default -> throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"Level not found.");
		};
	}
}
