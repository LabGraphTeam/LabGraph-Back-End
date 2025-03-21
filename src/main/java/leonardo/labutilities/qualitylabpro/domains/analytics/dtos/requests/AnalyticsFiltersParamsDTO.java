package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

public record AnalyticsFiltersParamsDTO(

        AnalyticsDateRangeParamsDTO dateRange,

        String validatedBy

) {}
