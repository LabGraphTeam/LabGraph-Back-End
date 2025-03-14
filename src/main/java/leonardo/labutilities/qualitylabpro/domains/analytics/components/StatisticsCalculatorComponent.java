package leonardo.labutilities.qualitylabpro.domains.analytics.components;

import java.util.List;

import org.springframework.stereotype.Component;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ComparativeErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;

@Component
public class StatisticsCalculatorComponent {

        // Systematic error percentage (bias) = ((calculated mean - mean) * 100) / mean
        // Random error percentage (inaccuracy) = 1.65 * coefficient of variation
        // Total error percentage = Random error percentage + Systematic error
        // percentage (bias)

        private StatisticsCalculatorComponent() {}

        public static MeanAndStdDeviationDTO calculateMeanAndStandardDeviation(
                List<AnalyticsDTO> values) {
                return computeStatistics(extractRecordValues(values));
        }

        public static List<Double> extractRecordValues(List<AnalyticsDTO> records) {
                return records.stream().map(AnalyticsDTO::value).toList();
        }

        public static double calculateCoefficientOfVariation(double standartDeviation,
                                                             double mean) {
                return (standartDeviation / mean) * 100;
        }

        public static double calculateSistematicErrorPercentage(double calculatedMean,
                                                                double mean) {
                return ((calculatedMean - mean) * 100) / mean;
        }

        public static double calculateRamdonErrorPercentage(double coefficientOfVariation) {
                return 1.65 * coefficientOfVariation;
        }

        public static double calculateTotalErrorPercentage(double randomErrorPercentage,
                                                           double sistematicErrorPercentage) {
                return randomErrorPercentage + sistematicErrorPercentage;
        }

        public static MeanAndStdDeviationDTO computeStatistics(List<Double> values) {

                var calculetedMean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);

                var calculetedStdDeviation = Math.sqrt(values.stream().mapToDouble(Double::doubleValue)
                                .map(value -> Math.pow(value - calculetedMean, 2)).sum() / values.size());

                return new MeanAndStdDeviationDTO(calculetedMean, calculetedStdDeviation);
        }

        public static ErrorStatisticsDTO calculateErrorStatistics(
                List<AnalyticsDTO> analyticsList,
                String defaultName,
                String defaultLevel, double defaultMean) {

                MeanAndStdDeviationDTO calculatedStatistics =
                                calculateMeanAndStandardDeviation(analyticsList);

                double inaccuracyPercetage =
                                calculateCoefficientOfVariation(calculatedStatistics.standardDeviation(),
                                                calculatedStatistics.mean());
                double sistematicErrorPercentage =
                                ((calculatedStatistics.mean() - defaultMean) * 100) / defaultMean;

                double randomErrorPercentage = calculateRamdonErrorPercentage(inaccuracyPercetage);

                double totalErrorPercentage =
                                calculateTotalErrorPercentage(randomErrorPercentage, sistematicErrorPercentage);

                return new ErrorStatisticsDTO(defaultName, defaultLevel, defaultMean, inaccuracyPercetage,
                                sistematicErrorPercentage, randomErrorPercentage, totalErrorPercentage,
                                analyticsList.size());
        }

        public static ComparativeErrorStatisticsDTO calculateComparativeErrorStatistics(
                String defaultName, String defaultLevel, double defaultMean,
                List<AnalyticsDTO> firstAnalyticsList,
                List<AnalyticsDTO> secondAnalyticsList, List<String> monthList) {

                ErrorStatisticsDTO firstErrorStatistics = calculateErrorStatistics(firstAnalyticsList,
                                defaultName, defaultLevel, defaultMean);

                ErrorStatisticsDTO secondErrorStatistics = calculateErrorStatistics(secondAnalyticsList,
                                defaultName, defaultLevel, defaultMean);

                double enhancedPercentage =
                                firstErrorStatistics.inaccuracyPercetage()
                                                - secondErrorStatistics.inaccuracyPercetage();

                return new ComparativeErrorStatisticsDTO(defaultName, defaultLevel, monthList.get(0),
                                monthList.get(1), enhancedPercentage);

        }

}
