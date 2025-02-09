package leonardo.labutilities.qualitylabpro.services.analytics;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import leonardo.labutilities.qualitylabpro.utils.components.ControlRulesValidators;
import leonardo.labutilities.qualitylabpro.utils.exception.CustomGlobalErrorHandling;

@Service
public class BiochemistryAnalyticService extends AbstractAnalyticHelperService {

	public BiochemistryAnalyticService(AnalyticsRepository analyticsRepository,
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


	@Override
	public List<AnalyticsDTO> findAnalyticsByNameAndLevelAndDate(String name, String level,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable) {
		return this.findAnalyticsByNameLevelAndDate(name, this.convertLevel(level), dateStart,
				dateEnd, pageable);
	}

	@Override
	public String convertLevel(String inputLevel) {
		return switch (inputLevel) {
			case "1" -> "PCCC1";
			case "2" -> "PCCC2";
			default -> throw new CustomGlobalErrorHandling.ResourceNotFoundException(
					"Level not found.");
		};
	}
}
