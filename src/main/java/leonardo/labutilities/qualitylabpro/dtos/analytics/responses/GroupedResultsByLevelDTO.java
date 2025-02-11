package leonardo.labutilities.qualitylabpro.dtos.analytics.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object containing grouped analytical results by control level")
public record GroupedResultsByLevelDTO(@Schema(
        description = "Grouped measured values organized by control level",
        requiredMode = Schema.RequiredMode.REQUIRED) GroupedValuesByLevelDTO groupedValuesByLevelDTO,

        @Schema(description = "Statistical data (mean and standard deviation) grouped by control level",
                requiredMode = Schema.RequiredMode.REQUIRED) GroupedMeanAndStdByLevelDTO groupedMeanAndStdByLevelDTO) {
}
