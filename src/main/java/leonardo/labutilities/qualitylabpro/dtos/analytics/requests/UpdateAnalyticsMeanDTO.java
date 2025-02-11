package leonardo.labutilities.qualitylabpro.dtos.analytics.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for updating analytics mean values")
public record UpdateAnalyticsMeanDTO(
		@Schema(description = "Name of the analytic test", example = "Glucose",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String name,

		@Schema(description = "Control level identifier", example = "1",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String level,

		@Schema(description = "Control level lot number", example = "LOT123",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String levelLot,

		@Schema(description = "New mean value to be updated", example = "118.3",
				requiredMode = Schema.RequiredMode.REQUIRED,
				minimum = "0.1") @NotNull double mean) {
}
