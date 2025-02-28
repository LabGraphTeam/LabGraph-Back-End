package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;

@Schema(description = "Data Transfer Object for Analytics information and measurements")
public record AnalyticsDTO(
                @Schema(description = "Unique identifier for the analytic record",
                                example = "1") Long id,

                @Schema(description = "Date and time when the analysis was performed",
                                example = "2023-12-20 14:30:00") @NotNull LocalDateTime date,

                @Schema(description = "Control level lot number", example = "LOT123",
                                requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String level_lot,

                @Schema(description = "Test lot number", example = "TEST456",
                                requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String test_lot,

                @Schema(description = "Name of the analytic test", example = "Glucose",
                                requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String name,

                @Schema(description = "Control level identifier", example = "1",
                                requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String level,

                @Schema(description = "Measured value of the analysis", example = "120.5",
                                requiredMode = Schema.RequiredMode.REQUIRED) @NotNull Double value,

                @Schema(description = "Mean value for the control level", example = "118.3",
                                requiredMode = Schema.RequiredMode.REQUIRED) @NotNull Double mean,

                @Schema(description = "Standard deviation for the control level", example = "2.5",
                                requiredMode = Schema.RequiredMode.REQUIRED) @NotNull Double sd,

                @Schema(description = "Unit of measurement", example = "mg/dL",
                                requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String unit_value,

                @Schema(description = "Westgard rules violated, if any",
                                example = "-2s") String rules,

                @Schema(description = "Additional description or notes about the analysis",
                                example = "Within acceptable range") String description) {

        public AnalyticsDTO(Analytic analytic) {
                this(analytic.getId(), analytic.getMeasurementDate(), analytic.getControlLevelLot(),
                                analytic.getReagentLot(), analytic.getTestName(),
                                analytic.getControlLevel(), analytic.getMeasurementValue(),
                                analytic.getTargetMean(), analytic.getStandardDeviation(),
                                analytic.getMeasurementUnit(), analytic.getControlRules(),
                                analytic.getDescription());

        }

}
