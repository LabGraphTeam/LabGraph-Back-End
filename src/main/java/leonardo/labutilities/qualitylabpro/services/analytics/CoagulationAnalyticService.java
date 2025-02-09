package leonardo.labutilities.qualitylabpro.services.analytics;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.components.ControlRulesValidators;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;

@Service
public class CoagulationAnalyticService extends AbstractAnalyticHelperService {

	public CoagulationAnalyticService(AnalyticsRepository analyticsRepository,
			EmailService emailService, ControlRulesValidators controlRulesValidators) {
		super(analyticsRepository, emailService, controlRulesValidators);
	}

	public Page<AnalyticsDTO> findAnalyticsByNameInByLevel(List<String> names, String level,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		return this.findAnalyticsByNameInByLevelBaseMethod(names, this.convertLevel(level),
				startDate, endDate, pageable);
	}

	@Override
	public List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable, String name,
			String level) {
		this.ensureNameExists(name);
		return this.findAnalyticsByNameAndLevelWithPagination(pageable, name,
				this.convertLevel(level));
	}

	@Cacheable(value = "analyticsByNameLevelAndDate",
			key = "{#name, #level, #dateStart, #dateEnd, #pageable.pageNumber, #pageable.pageSize}")
	@Override
	public List<AnalyticsDTO> findAnalyticsByNameAndLevelAndDate(String name, String level,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
		return this.findAnalyticsByNameLevelAndDate(name, this.convertLevel(level), dateStart,
				dateEnd, pageable);
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
