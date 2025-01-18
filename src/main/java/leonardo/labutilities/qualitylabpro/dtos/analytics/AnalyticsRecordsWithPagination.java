package leonardo.labutilities.qualitylabpro.dtos.analytics;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public record AnalyticsRecordsWithPagination(Page<AnalyticsRecord> analyticsRecord,
                                             PaginationRecord paginationRecord) {
}
