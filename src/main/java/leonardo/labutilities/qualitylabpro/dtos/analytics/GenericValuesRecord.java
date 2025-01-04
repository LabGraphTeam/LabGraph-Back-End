package leonardo.labutilities.qualitylabpro.dtos.analytics;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import leonardo.labutilities.qualitylabpro.entities.GenericAnalytics;

public record GenericValuesRecord(Long id,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") @NotNull LocalDateTime date,
        @NotNull String level_lot, @NotNull String test_lot, @NotNull String name,
        @NotNull String level, @NotNull Double value, @NotNull Double mean, @NotNull Double sd,
        @NotNull String unit_value, String rules, String description) {
    @JsonIgnore
    public Long id() {
        return id;
    }

    public GenericValuesRecord(GenericAnalytics analytics) {
        this(analytics.getId(), analytics.getDate(), analytics.getLevelLot(),
                analytics.getTestLot(), analytics.getName(), analytics.getLevel(),
                analytics.getValue(), analytics.getMean(), analytics.getSd(),
                analytics.getUnitValue(), analytics.getRules(), analytics.getDescription());
    }
}
