package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses;

public record ComparativeErrorStatisticsDTO(
                String analyticName,
                String analyticLevel,
                String firstMonth,
                String secondMonth,
                Double enhancedPercentage) {}
