package leonardo.labutilities.qualitylabpro.dtos.analytics.responses;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for statistical data (mean and standard deviation) grouped by control level")
public record GroupedMeanAndStdByLevelDTO(
		@Schema(description = "Control level identifier", example = "Level 1",
				requiredMode = Schema.RequiredMode.REQUIRED) String level,

		@Schema(description = "List of mean and standard deviation calculations for this control level",
				requiredMode = Schema.RequiredMode.REQUIRED) List<MeanAndStdDeviationDTO> values) {
}
