package leonardo.labutilities.qualitylabpro.dtos.analytics;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import leonardo.labutilities.qualitylabpro.entities.Analytic;


public record AnalyticsDTO(Long id,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @NotNull LocalDateTime date,
        @NotNull String level_lot, @NotNull String test_lot, @NotNull String name,
        @NotNull String level, @NotNull Double value, @NotNull Double mean, @NotNull Double sd,
        @NotNull String unit_value, String rules, String description) {
    public AnalyticsDTO(Analytic analytic) {
        this(analytic.getId(), analytic.getDate(), analytic.getLevelLot(), analytic.getTestLot(),
                analytic.getName(), analytic.getLevel(), analytic.getValue(), analytic.getMean(),
                analytic.getSd(), analytic.getUnitValue(), analytic.getRules(),
                analytic.getDescription());
    }
}
