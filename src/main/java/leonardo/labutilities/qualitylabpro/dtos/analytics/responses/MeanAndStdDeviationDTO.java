package leonardo.labutilities.qualitylabpro.dtos.analytics.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object containing statistical calculations for quality control measurements")
public record MeanAndStdDeviationDTO(
		@Schema(description = "Average value of the measurements", example = "118.3",
				requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0") double mean,

		@Schema(description = "Standard deviation showing the dispersion of measurements",
				example = "2.5", requiredMode = Schema.RequiredMode.REQUIRED,
				minimum = "0") double standardDeviation) {
}
