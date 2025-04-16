package leonardo.labutilities.qualitylabpro.domains.analytic.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.analytics.utils.AnalyticRulesValidation;
import leonardo.labutilities.qualitylabpro.domains.shared.blacklist.AnalyticsBlackList;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling;

@ExtendWith(MockitoExtension.class)
class AnalyticObjectValidationComponentTests {

    @Test
    void validateResultsNotEmpty_shouldNotThrowException_whenResultsNotEmpty() {
        // Arrange
        List<String> results = List.of("result1", "result2");

        // Act & Assert
        assertDoesNotThrow(
                () -> AnalyticRulesValidation.validateResultsNotEmpty(results, "Results not found"));
    }

    @Test
    void validateResultsNotEmpty_shouldThrowException_whenResultsEmpty() {
        // Arrange
        List<String> results = List.of();
        String errorMessage = "Results not found";

        // Act & Assert
        CustomGlobalErrorHandling.ResourceNotFoundException exception =
                assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
                        () -> AnalyticRulesValidation.validateResultsNotEmpty(results, errorMessage));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void validateResultsNotEmpty_shouldThrowException_whenResultsNull() {
        // Arrange
        List<String> results = null;
        String errorMessage = "Results not found";

        // Act & Assert
        CustomGlobalErrorHandling.ResourceNotFoundException exception =
                assertThrows(CustomGlobalErrorHandling.ResourceNotFoundException.class,
                        () -> AnalyticRulesValidation.validateResultsNotEmpty(results, errorMessage));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void isRuleBroken_shouldReturnTrue_whenControlRuleInRules() {
        // Arrange
        Analytic analytic = mock(Analytic.class);
        when(analytic.getControlRules()).thenReturn("+3s");

        // Act
        boolean result = AnalyticRulesValidation.isRuleBroken(analytic);

        // Assert
        assertTrue(result);
        verify(analytic).getControlRules();
    }

    @ParameterizedTest
    @ValueSource(strings = {"+1s", "-1s", "+4s"})
    @NullSource
    void isRuleBroken_shouldReturnFalse_whenControlRuleIsNotBroken(String rule) {
        // Arrange
        Analytic analytic = mock(Analytic.class);
        when(analytic.getControlRules()).thenReturn(rule);

        // Act
        boolean result = AnalyticRulesValidation.isRuleBroken(analytic);

        // Assert
        assertFalse(result);
        verify(analytic).getControlRules();
    }

    @Test
    void filterFailedRecords_shouldReturnFilteredList_whenRecordsExist() {
        // Arrange
        List<Analytic> records = new ArrayList<>();

        Analytic brokenRule = mock(Analytic.class);
        when(brokenRule.getControlRules()).thenReturn("+2s");
        when(brokenRule.getTestName()).thenReturn("Test1");

        Analytic notBrokenRule = mock(Analytic.class);
        when(notBrokenRule.getControlRules()).thenReturn("+1s");

        Analytic blacklistedTest = mock(Analytic.class);
        when(blacklistedTest.getControlRules()).thenReturn("-2s");
        when(blacklistedTest.getTestName()).thenReturn(AnalyticsBlackList.BLACK_LIST.toString());

        records.add(brokenRule);
        records.add(notBrokenRule);
        records.add(blacklistedTest);

        // Act
        List<Analytic> result = AnalyticRulesValidation.filterFailedRecords(records);

        // Assert
        assertEquals(1, result.size());
        assertSame(brokenRule, result.get(0));
    }

    @Test
    void filterFailedRecords_shouldReturnEmptyList_whenNoRecordsPassFilter() {
        // Arrange
        List<Analytic> records = new ArrayList<>();

        Analytic notBrokenRule = mock(Analytic.class);
        when(notBrokenRule.getControlRules()).thenReturn("+1s");

        records.add(notBrokenRule);

        // Act
        List<Analytic> result = AnalyticRulesValidation.filterFailedRecords(records);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void filterFailedRecords_shouldReturnEmptyList_whenRecordsNull() {
        // Act
        List<Analytic> result = AnalyticRulesValidation.filterFailedRecords(null);

        // Assert
        assertTrue(result.isEmpty());
    }
}
