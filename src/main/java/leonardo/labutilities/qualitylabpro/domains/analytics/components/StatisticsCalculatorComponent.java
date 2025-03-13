package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import java.util.List;

import org.springframework.stereotype.Component;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ComparativeErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;

@Component
public final class StatisticsCalculatorComponent {

        // Systematic error percentage (bias) = ((calculated mean - mean) * 100) / mean
        // Random error percentage (inaccuracy) = 1.65 * coefficient of variation
        // Total error percentage = Random error percentage + Systematic error
        // percentage (bias)

        private StatisticsCalculatorComponent() {}

        public static MeanAndStdDeviationDTO calculateMeanAndStandardDeviation(final List<AnalyticsDTO> values) {
                return computeStatistics(extractRecordValues(values));
        }

        public static List<Double> extractRecordValues(final List<AnalyticsDTO> records) {
                return records.stream().map(AnalyticsDTO::value).toList();
        }

        public static double calculateCoefficientOfVariation(final double standartDeviation, final double mean) {
                return (standartDeviation / mean) * 100;
        }

        public static double calculateSistematicErrorPercentage(final double calculatedMean, final double mean) {
                return ((calculatedMean - mean) * 100) / mean;
        }

        public static double calculateRamdonErrorPercentage(final double coefficientOfVariation) {
                return 1.65 * coefficientOfVariation;
        }

        public static double calculateTotalErrorPercentage(final double randomErrorPercentage,
                        final double sistematicErrorPercentage) {
                return randomErrorPercentage + sistematicErrorPercentage;
        }

        public static MeanAndStdDeviationDTO computeStatistics(final List<Double> values) {

                final double calculetedMean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                final double calculetedStdDeviation = Math.sqrt(values.stream().mapToDouble(Double::doubleValue)
                                .map(value -> Math.pow(value - calculetedMean, 2)).sum() / values.size());

                return new MeanAndStdDeviationDTO(calculetedMean, calculetedStdDeviation);
        }

        public static ErrorStatisticsDTO calculateErrorStatistics(final List<AnalyticsDTO> analyticsList,
                        final String defaultName, final String defaultLevel, final double defaultMean) {

                MeanAndStdDeviationDTO calculatedStatistics = calculateMeanAndStandardDeviation(analyticsList);

                final double inaccuracyPercetage = calculateCoefficientOfVariation(
                                calculatedStatistics.standardDeviation(), calculatedStatistics.mean());
                final double sistematicErrorPercentage =
                                ((calculatedStatistics.mean() - defaultMean) * 100) / defaultMean;

                final double randomErrorPercentage = calculateRamdonErrorPercentage(inaccuracyPercetage);

                final double totalErrorPercentage =
                                calculateTotalErrorPercentage(randomErrorPercentage, sistematicErrorPercentage);

                return new ErrorStatisticsDTO(defaultName, defaultLevel, defaultMean, inaccuracyPercetage,
                                sistematicErrorPercentage, randomErrorPercentage, totalErrorPercentage,
                                analyticsList.size());
        }

        public static ComparativeErrorStatisticsDTO calculateComparativeErrorStatistics(final String defaultName,
                        final String defaultLevel, double defaultMean, final List<AnalyticsDTO> firstAnalyticsList,
                        final List<AnalyticsDTO> secondAnalyticsList, final List<String> monthList) {

                ErrorStatisticsDTO firstErrorStatistics =
                                calculateErrorStatistics(firstAnalyticsList, defaultName, defaultLevel, defaultMean);

                ErrorStatisticsDTO secondErrorStatistics =
                                calculateErrorStatistics(secondAnalyticsList, defaultName, defaultLevel, defaultMean);

                final double enhancedPercentage = firstErrorStatistics.inaccuracyPercetage()
                                - secondErrorStatistics.inaccuracyPercetage();

                return new ComparativeErrorStatisticsDTO(defaultName, defaultLevel, monthList.get(0), monthList.get(1),
                                enhancedPercentage);

        }

}
