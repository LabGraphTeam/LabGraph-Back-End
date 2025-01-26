package leonardo.labutilities.qualitylabpro.dtos.analytics;

import org.springframework.data.domain.Page;

public record AnalyticsWithPaginationDTO(Page<AnalyticsDTO> analyticsRecord,
                                         PaginationDTO paginationDTO) {
}
