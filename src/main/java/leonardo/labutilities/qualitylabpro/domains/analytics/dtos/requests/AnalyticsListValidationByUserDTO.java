package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import java.util.List;

public record AnalyticsListValidationByUserDTO(
        List<Long> ids) {}
