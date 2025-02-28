package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for filtering analytics by date range")
public record AnalyticsDateRangeParamsDTO(
		@Schema(description = "Start date for the date range filter",
				example = "2023-01-01 00:00:00",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull LocalDateTime startDate,

		@Schema(description = "End date for the date range filter", example = "2023-12-31 23:59:59",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull LocalDateTime endDate) {}
