package leonardo.labutilities.qualitylabpro.domains.analytics.utils;

import java.util.List;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ComparativeErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.ErrorStatisticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.MeanAndStdDeviationDTO;

public final class StatisticsCalculatorUtility {

        private StatisticsCalculatorUtility() {}

        public static MeanAndStdDeviationDTO calculateMeanAndStandardDeviation(List<AnalyticsDTO> values) {
                return computeStatistics(extractRecordValues(values));
        }

        public static List<Double> extractRecordValues(List<AnalyticsDTO> records) {
                return records.stream().map(AnalyticsDTO::value).toList();
        }

        public static double calculateCoefficientOfVariation(double standardDeviation, double mean) {
                return (standardDeviation / mean) * 100;
        }

        public static double calculateSystematicErrorPercentage(double calculatedMean, double mean) {
                return ((calculatedMean - mean) * 100) / mean;
        }

        public static double calculateRandomErrorPercentage(double coefficientOfVariation) {
                return 1.65 * coefficientOfVariation;
        }

        public static double calculateTotalErrorPercentage(double randomErrorPercentage,
                        double systematicErrorPercentage) {
                return randomErrorPercentage + systematicErrorPercentage;
        }

        public static MeanAndStdDeviationDTO computeStatistics(final List<Double> values) {

                final double mean = values.stream()
                                .mapToDouble(Double::doubleValue)
                                .average().orElse(0.0);

                final double standardDeviation = Math.sqrt(values.stream()
                                .mapToDouble(Double::doubleValue)
                                .map(value -> Math.pow(value - mean, 2))
                                .sum() / values.size());

                return new MeanAndStdDeviationDTO(mean, standardDeviation);
        }

        public static ErrorStatisticsDTO calculateErrorStatistics(
                        List<AnalyticsDTO> analyticsList, String defaultName, String defaultLevel, double defaultMean) {
                MeanAndStdDeviationDTO calculatedStatistics = calculateMeanAndStandardDeviation(analyticsList);

                final double inaccuracyPercentage = calculateCoefficientOfVariation(
                                calculatedStatistics.standardDeviation(), calculatedStatistics.mean());

                final double systematicErrorPercentage = calculateSystematicErrorPercentage(
                                calculatedStatistics.mean(), defaultMean);

                final double randomErrorPercentage = calculateRandomErrorPercentage(inaccuracyPercentage);
                final double totalErrorPercentage =
                                calculateTotalErrorPercentage(randomErrorPercentage, systematicErrorPercentage);

                return new ErrorStatisticsDTO(
                                defaultName, defaultLevel, defaultMean, inaccuracyPercentage, systematicErrorPercentage,
                                randomErrorPercentage, totalErrorPercentage, analyticsList.size());
        }

        public static ComparativeErrorStatisticsDTO calculateComparativeErrorStatistics(
                        String defaultName, String defaultLevel, double defaultMean,
                        List<AnalyticsDTO> firstAnalyticsList,
                        List<AnalyticsDTO> secondAnalyticsList, List<String> monthList) {
                ErrorStatisticsDTO firstErrorStatistics =
                                calculateErrorStatistics(firstAnalyticsList, defaultName, defaultLevel, defaultMean);
                ErrorStatisticsDTO secondErrorStatistics =
                                calculateErrorStatistics(secondAnalyticsList, defaultName, defaultLevel, defaultMean);

                final double enhancedPercentage = firstErrorStatistics.inaccuracyPercetage()
                                - secondErrorStatistics.inaccuracyPercetage();

                return new ComparativeErrorStatisticsDTO(
                                defaultName, defaultLevel, monthList.get(0), monthList.get(1), enhancedPercentage);
        }
}
