package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;

@Component
public class StatisticsCalcComponent {

    private StatisticsCalcComponent() {}

    public static ErrorStatisticsDTO calculateErrorStatistics(List<AnalyticsDTO> analyticsList) {

        return Optional.ofNullable(analyticsList).filter(list -> !list.isEmpty()).map(list -> {
            var first = list.get(0);
            var stats = calcMeanAndStandardDeviationOptimized(list);

            return new ErrorStatisticsDTO(first.name(), first.level(), stats.mean(),
                    stats.standardDeviation(), ((stats.mean() - first.mean()) * 100) / first.mean(),
                    1.65 * (stats.standardDeviation() / stats.mean()),
                    ((stats.mean() - first.mean()) * 100) / first.mean()
                            + 1.65 * (stats.standardDeviation() / stats.mean()),
                    list.size());
        }).orElseThrow(() -> new IllegalArgumentException("Analytics list cannot be empty"));
    }

    public static final MeanAndStdDeviationDTO computeStatistics(List<Double> values) {

        return Optional.ofNullable(values).filter(list -> !list.isEmpty()).map(list -> {

            var stats = list.stream().mapToDouble(Double::doubleValue).summaryStatistics();

            double mean = stats.getAverage();

            double stdDev = Math.sqrt(list.stream().mapToDouble(value -> Math.pow(value - mean, 2))
                    .average().orElse(0.0));

            return new MeanAndStdDeviationDTO(mean, stdDev);
        }).orElse(new MeanAndStdDeviationDTO(0.0, 0.0));
    }

    public static final MeanAndStdDeviationDTO calcMeanAndStandardDeviationOptimized(
            List<AnalyticsDTO> values) {
        return computeStatistics(extractRecordValues(values));
    }

    public static final List<Double> extractRecordValues(List<AnalyticsDTO> records) {
        return records.stream().map(AnalyticsDTO::value).toList();
    }

}
