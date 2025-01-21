package leonardo.labutilities.qualitylabpro.dtos.analytics;

import org.springframework.data.domain.Page;

public record AnalyticsRecordsWithPagination(Page<AnalyticsRecord> analyticsRecord,
		PaginationRecord paginationRecord) {
}
