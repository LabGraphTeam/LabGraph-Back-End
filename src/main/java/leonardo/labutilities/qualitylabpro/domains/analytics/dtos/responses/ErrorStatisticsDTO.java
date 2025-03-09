package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses;

public record ErrorStatisticsDTO(String analyticName, String analyticLevel, Double calculatedMean,
        Double inaccuracyPercetage, Double sistematicErrorPercentage, Double randomErrorPercentage,
        Double totalErrorPercentage, Integer totalMeasurements) {

    public ErrorStatisticsDTO {
        if (calculatedMean == null || inaccuracyPercetage == null) {
            throw new IllegalArgumentException(
                    "calculatedMean and calculatedStandardDeviation must not be null");
        }
    }
}
