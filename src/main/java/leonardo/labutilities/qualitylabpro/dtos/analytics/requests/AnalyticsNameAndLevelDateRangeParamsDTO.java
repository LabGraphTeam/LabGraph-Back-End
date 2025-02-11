package leonardo.labutilities.qualitylabpro.dtos.analytics.requests;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for filtering analytics by name, level and date range")
public record AnalyticsNameAndLevelDateRangeParamsDTO(
		@Schema(description = "Name of the analytic test to filter", example = "Glucose",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String name,

		@Schema(description = "Control level identifier to filter", example = "Level 1",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String level,

		@Schema(description = "Start date for the date range filter",
				example = "2023-01-01T00:00:00",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull LocalDateTime startDate,

		@Schema(description = "End date for the date range filter", example = "2023-12-31T23:59:59",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull LocalDateTime endDate) {
}
