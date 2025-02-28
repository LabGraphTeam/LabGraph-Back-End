package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import java.util.List;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;

public class StatisticsCalcComponent {

    private StatisticsCalcComponent() {}

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
