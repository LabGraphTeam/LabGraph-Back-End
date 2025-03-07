package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;

@Schema(description = "Data Transfer Object combining analytics measurements with their statistical calculations")
public record AnalyticsWithCalcDTO(
		@Schema(description = "List of analytical measurements",
				requiredMode = Schema.RequiredMode.REQUIRED) List<AnalyticsDTO> analyticsDTO,

		@Schema(description = "Statistical calculations (mean and standard deviation) for the measurements",
				requiredMode = Schema.RequiredMode.REQUIRED) MeanAndStdDeviationDTO calcMeanAndStdDTO) {}
