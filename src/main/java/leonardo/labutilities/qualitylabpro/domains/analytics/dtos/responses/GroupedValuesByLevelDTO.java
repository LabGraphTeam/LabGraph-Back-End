package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for analytics measurements grouped by control level")
public record GroupedValuesByLevelDTO(
		@Schema(description = "Control level identifier", example = "Level 1",
				requiredMode = Schema.RequiredMode.REQUIRED) String level,

		@Schema(description = "List of analytical measurements for this control level",
				requiredMode = Schema.RequiredMode.REQUIRED) List<AnalyticsDTO> values) {}
