package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import java.util.List;
import org.springframework.stereotype.Component;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;

@Component
public class StatisticsCalcComponent {

    private StatisticsCalcComponent() {}

    // Systematic error percentage (bias) = ((calculated mean - mean) * 100) / mean
    // Random error percentage (inaccuracy) = 1.65 * coefficient of variation
    // Total error percentage = Random error percentage + Systematic error percentage (bias)

    public static ErrorStatisticsDTO calculateErrorStatistics(List<AnalyticsDTO> analyticsList) {

        var level = analyticsList.get(0).level();

        var name = analyticsList.get(0).name();

        var mean = analyticsList.get(0).mean();

        var calculatedMean =
                analyticsList.stream().mapToDouble(AnalyticsDTO::value).average().orElse(0.0);

        var calculatedStandardDeviation = Math.sqrt(analyticsList.stream()
                .mapToDouble(analytic -> Math.pow(analytic.value() - calculatedMean, 2)).average()
                .orElse(0.0));

        var coefficientOfVariation = calculatedStandardDeviation / calculatedMean;

        var totalMeasurements = analyticsList.size();

        var sistematicErrorPercentage = (((calculatedMean - mean) * 100) / mean);

        var randomErrorPercentage = 1.65 * coefficientOfVariation;

        var totalErrorPercentage = sistematicErrorPercentage + randomErrorPercentage;

        return new ErrorStatisticsDTO(name, level, calculatedMean, calculatedStandardDeviation,
                (sistematicErrorPercentage), (randomErrorPercentage), (totalErrorPercentage),
                totalMeasurements);
    }

    public static final MeanAndStdDeviationDTO computeStatistics(List<Double> values) {
        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        int size = values.size();
        double mean = sum / size;
        double variance = values.stream().mapToDouble(value -> Math.pow(value - mean, 2)).average()
                .orElse(0.0);
        return new MeanAndStdDeviationDTO(mean, Math.sqrt(variance));
    }

    public static final MeanAndStdDeviationDTO calcMeanAndStandardDeviationOptimized(
            List<AnalyticsDTO> values) {
        return computeStatistics(extractRecordValues(values));
    }

    public static final List<Double> extractRecordValues(List<AnalyticsDTO> records) {
        return records.stream().map(AnalyticsDTO::value).toList();
    }

}
