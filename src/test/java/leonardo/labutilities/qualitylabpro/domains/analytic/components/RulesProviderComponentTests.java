package leonardo.labutilities.qualitylabpro.domains.analytic.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import leonardo.labutilities.qualitylabpro.domains.analytics.components.RulesProviderComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks;

@ExtendWith(MockitoExtension.class)
class RulesProviderComponentTests {

        @Mock
        private AnalyticsRepository analyticsRepository;

        private RulesProviderComponent rulesProviderComponent;

        @BeforeEach
        void setUp() {
                rulesProviderComponent = new RulesProviderComponent(analyticsRepository);
        }

        @Test
        void validateRules_WithEmptyList_ShouldReturnDefaultMessage() {
                // Arrange
                List<AnalyticsDTO> emptyList = Collections.emptyList();

                // Act
                String result = rulesProviderComponent.validateRules(emptyList);

                // Assert
                assertTrue(result.contains("No analytics data provided"));
                verify(analyticsRepository, never()).findLast10ByTestNameAndControlLevel(anyString(),
                                anyString());
        }

        @Test
        void validateRules_WithNullList_ShouldReturnDefaultMessage() {
                // Act
                String result = rulesProviderComponent.validateRules(null);

                // Assert
                assertTrue(result.contains("No analytics data provided"));
                verify(analyticsRepository, never()).findLast10ByTestNameAndControlLevel(anyString(),
                                anyString());
        }

        @Test
        void validateRules_WithOneThreeSigmaViolation_ShouldReturnAppropriateError() {
                // Arrange
                String testName = "Glucose";
                String level = "Normal";

                AnalyticsDTO inputAnalytic = createAnalyticsDTO(testName, level, 100.0, 10.0, 2.0);
                List<AnalyticsDTO> inputAnalytics = List.of(inputAnalytic);

                // Create violation data - value exceeds mean + 3*SD (100 + 3*2 = 106)
                List<AnalyticsDTO> repositoryLastData = createAnalyticsList(testName, level,
                                new double[] {107.0, 102.0, 103.0});

                when(analyticsRepository.findLast10ByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryLastData);
                when(analyticsRepository.findLastByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryLastData);

                // Act
                String result = rulesProviderComponent.validateRules(inputAnalytics);

                // Assert
                assertTrue(result.contains("1-3s"));
                assertTrue(result.contains("One observation exceeds mean ±3 SD"));
                assertTrue(result.contains(testName));
                assertTrue(result.contains(level));
        }

        @Test
        void validateRules_WithFourOneSigmaViolation_ShouldReturnAppropriateError() {
                // Arrange
                String testName = "Glucose";
                String level = "Normal";

                AnalyticsDTO inputAnalytic = createAnalyticsDTO(testName, level, 100.0, 10.0, 2.0);
                List<AnalyticsDTO> inputAnalytics = List.of(inputAnalytic);

                // Create violation data - 4 consecutive values above mean + 1*SD (100 + 1*2 = 102)
                List<AnalyticsDTO> repositoryData = createAnalyticsList(testName, level,
                                new double[] {103.0, 103.0, 103.0, 103.0, 99.0});

                when(analyticsRepository.findLast10ByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryData);
                when(analyticsRepository.findLastByTestNameAndControlLevel(testName, level))
                                .thenReturn(Collections.singletonList(inputAnalytic));

                // Act
                String result = rulesProviderComponent.validateRules(inputAnalytics);

                // Assert
                assertTrue(result.contains("4-1s"));
                assertTrue(result.contains("Four consecutive measurements exceed ±1 SD"));
                assertTrue(result.contains(testName));
                assertTrue(result.contains(level));
        }

        @Test
        void validateRules_WithTenConsecutiveViolation_ShouldReturnAppropriateError() {
                // Arrange
                String testName = "Glucose";
                String level = "Normal";

                AnalyticsDTO inputAnalytic = createAnalyticsDTO(testName, level, 100.0, 10.0, 2.0);
                List<AnalyticsDTO> inputAnalytics = List.of(inputAnalytic);

                // Create violation data - 10 consecutive values above mean + 1*SD (100 + 1*2 = 102)
                double[] tenHighValues =
                                {103.0, 103.0, 103.0, 103.0, 103.0, 103.0, 103.0, 103.0, 103.0, 103.0};
                List<AnalyticsDTO> repositoryData =
                                createAnalyticsList(testName, level, tenHighValues);

                when(analyticsRepository.findLast10ByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryData);
                when(analyticsRepository.findLastByTestNameAndControlLevel(testName, level))
                                .thenReturn(Collections.singletonList(inputAnalytic));

                // Act
                String result = rulesProviderComponent.validateRules(inputAnalytics);

                // Assert
                assertTrue(result.contains("10x"));
                assertTrue(result.contains("Ten consecutive measurements on same side of mean"));
                assertTrue(result.contains(testName));
                assertTrue(result.contains(level));
        }

        @Test
        void validateRules_WithMultipleViolations_ShouldReportAllViolations() {
                // Arrange
                String testName = "Glucose";
                String level = "Normal";

                AnalyticsDTO inputAnalytic = createAnalyticsDTO(testName, level, 100.0, 10.0, 2.0);
                List<AnalyticsDTO> inputAnalytics = List.of(inputAnalytic);

                // Create violation data with both 1-3s and 4-1s violations
                List<AnalyticsDTO> repositoryData = createAnalyticsList(testName, level,
                                new double[] {107.0, 103.0, 103.0, 103.0, 103.0});

                when(analyticsRepository.findLast10ByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryData);
                when(analyticsRepository.findLastByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryData);

                // Act
                String result = rulesProviderComponent.validateRules(inputAnalytics);

                // Assert
                assertTrue(result.contains("1-3s"));
                assertTrue(result.contains("4-1s"));
                assertTrue(result.contains("One observation exceeds mean ±3 SD"));
                assertTrue(result.contains("Four consecutive measurements exceed ±1 SD"));
        }

        @Test
        void validateRules_WithNoViolations_ShouldReturnNoErrors() {
                // Arrange
                String testName = "Glucose";
                String level = "Normal";

                AnalyticsDTO inputAnalytic = createAnalyticsDTO(testName, level, 100.0, 10.0, 2.0);
                List<AnalyticsDTO> inputAnalytics = List.of(inputAnalytic);

                // Create data with no violations
                List<AnalyticsDTO> repositoryData = createAnalyticsList(testName, level,
                                new double[] {101.0, 99.0, 100.5, 101.5, 99.5});

                when(analyticsRepository.findLast10ByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryData);
                when(analyticsRepository.findLastByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryData);

                // Act
                String result = rulesProviderComponent.validateRules(inputAnalytics);

                // Assert
                assertEquals("<div style='font-family: Arial, sans-serif;'></div>", result);
        }

        @Test
        void validateRules_WithMultipleAnalytics_ShouldReportEachViolationOnlyOnce() {
                // Arrange
                String testName = "Glucose";
                String level = "Normal";

                // Create two identical analytics records - should only report violations once
                AnalyticsDTO inputAnalytic1 = createAnalyticsDTO(testName, level, 100.0, 10.0, 2.0);
                AnalyticsDTO inputAnalytic2 = createAnalyticsDTO(testName, level, 100.0, 10.0, 2.0);
                List<AnalyticsDTO> inputAnalytics = List.of(inputAnalytic1, inputAnalytic2);

                // Create violation data
                List<AnalyticsDTO> repositoryData = createAnalyticsList(testName, level,
                                new double[] {107.0, 103.0, 103.0, 103.0, 103.0});

                when(analyticsRepository.findLast10ByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryData);
                when(analyticsRepository.findLastByTestNameAndControlLevel(testName, level))
                                .thenReturn(repositoryData);

                // Act
                String result = rulesProviderComponent.validateRules(inputAnalytics);

                // Assert
                // Count occurrences of the violation message
                int occurrencesOneThreeSigma = countOccurrences(result, "1-3s");
                int occurrencesFourOneSigma = countOccurrences(result, "4-1s");

                assertEquals(1, occurrencesOneThreeSigma, "1-3s rule should be reported only once");
                assertEquals(1, occurrencesFourOneSigma, "4-1s rule should be reported only once");
        }

        // Helper methods
        private AnalyticsDTO createAnalyticsDTO(String name, String level, double value, double mean,
                        double sd) {
                var sample = AnalyticsHelperMocks.createSampleRecord();
                return new AnalyticsDTO(sample.id(), sample.date(), sample.level_lot(), sample.test_lot(),

                                name, level, value, mean, sd, sample.unit_value(), sample.level_lot(),
                                sample.description(), sample.validator_user(), sample.owner_user());
        }

        private List<AnalyticsDTO> createAnalyticsList(String name, String level,
                        double[] values) {
                List<AnalyticsDTO> analytics = new ArrayList<>();
                for (double value : values) {
                        analytics.add(createAnalyticsDTO(name, level, value, 100.0, 2.0));
                }
                return analytics;
        }

        private int countOccurrences(String text, String searchString) {
                int count = 0;
                int index = 0;
                while ((index = text.indexOf(searchString, index)) != -1) {
                        count++;
                        index += searchString.length();
                }
                return count;
        }
}
