package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComparativeErrorStatisticsParamsDTO(
                @NotBlank @Schema(description = "Name of the analytic") String analyticName,

                @NotBlank @Schema(description = "Level of the analytic") String analyticLevel,

                @NotNull @Schema(description = "Start date for the first period",
                                example = "2024-01-01T00:00:00") @DateTimeFormat(
                                                iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime firstStartDate,

                @NotNull @Schema(description = "End date for the first period",
                                example = "2024-02-31T23:59:59") @DateTimeFormat(
                                                iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime firstEndDate,

                @NotNull @Schema(description = "Start date for the second period",
                                example = "2024-02-01T00:00:00") @DateTimeFormat(
                                                iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime secondStartDate,

                @NotNull @Schema(description = "End date for the second period",
                                example = "2024-02-31T23:59:59") @DateTimeFormat(
                                                iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime secondEndDate) {

}
