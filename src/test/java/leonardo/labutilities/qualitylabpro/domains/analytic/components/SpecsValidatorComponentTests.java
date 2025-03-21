package leonardo.labutilities.qualitylabpro.domains.analytic.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import leonardo.labutilities.qualitylabpro.domains.analytics.components.SpecsValidatorComponent;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.ThresholdAnalyticsRules;
import leonardo.labutilities.qualitylabpro.domains.analytics.constants.ValidationAnalyticsDescriptions;

class SpecsValidatorComponentTests {

    private SpecsValidatorComponent specsValidator;
    private final double MEAN = 100.0;
    private final double SD = 10.0;

    @BeforeEach
    void setUp() {
        specsValidator = new SpecsValidatorComponent();
    }

    @Test
    @DisplayName("Should return normal range when value is within mean")
    void testValueWithinNormalRange() {
        // Value between -1SD and +1SD
        double value = 105.0;

        specsValidator.validator(value, MEAN, SD);

        assertEquals("Approved according to current Westgard configured rules", specsValidator.getDescription());
        assertEquals("No rule broken", specsValidator.getRules());
    }

    @Test
    @DisplayName("Should detect value in +1SD range")
    void testValueInPositive1SDRange() {
        // Value just above +1SD
        double value = 110.1;

        specsValidator.validator(value, MEAN, SD);

        assertEquals(ValidationAnalyticsDescriptions.DESCRIPTIONS.get(0), specsValidator.getDescription());
        assertEquals(ThresholdAnalyticsRules.RULES.get(0), specsValidator.getRules());
    }

    @Test
    @DisplayName("Should detect value in +2SD range")
    void testValueInPositive2SDRange() {
        // Value above +2SD but below +3SD
        double value = 125.0;

        specsValidator.validator(value, MEAN, SD);

        assertEquals(ValidationAnalyticsDescriptions.DESCRIPTIONS.get(1), specsValidator.getDescription());
        assertEquals(ThresholdAnalyticsRules.RULES.get(1), specsValidator.getRules());
    }

    @Test
    @DisplayName("Should detect rule violation at +3SD")
    void testValueExceedsPositive3SD() {
        // Value above +3SD
        double value = 135.0;

        specsValidator.validator(value, MEAN, SD);

        assertEquals(ValidationAnalyticsDescriptions.DESCRIPTIONS.get(2), specsValidator.getDescription());
        assertEquals(ThresholdAnalyticsRules.RULES.get(2), specsValidator.getRules());
    }

    @Test
    @DisplayName("Should detect value in -1SD range")
    void testValueInNegative1SDRange() {
        // Value just below -1SD
        double value = 89.9;

        specsValidator.validator(value, MEAN, SD);

        assertEquals(ValidationAnalyticsDescriptions.DESCRIPTIONS.get(3), specsValidator.getDescription());
        assertEquals(ThresholdAnalyticsRules.RULES.get(3), specsValidator.getRules());
    }

    @Test
    @DisplayName("Should detect value in -2SD range")
    void testValueInNegative2SDRange() {
        // Value below -2SD but above -3SD
        double value = 75.0;

        specsValidator.validator(value, MEAN, SD);

        assertEquals(ValidationAnalyticsDescriptions.DESCRIPTIONS.get(4), specsValidator.getDescription());
        assertEquals(ThresholdAnalyticsRules.RULES.get(4), specsValidator.getRules());
    }

    @Test
    @DisplayName("Should detect rule violation at -3SD")
    void testValueExceedsNegative3SD() {
        // Value below -3SD
        double value = 65.0;

        specsValidator.validator(value, MEAN, SD);

        assertEquals(ValidationAnalyticsDescriptions.DESCRIPTIONS.get(5), specsValidator.getDescription());
        assertEquals(ThresholdAnalyticsRules.RULES.get(5), specsValidator.getRules());
    }

    @Test
    @DisplayName("Should handle edge case at exactly +1SD")
    void testValueExactlyAtPositive1SD() {
        double value = MEAN + SD; // Exactly at +1SD

        specsValidator.validator(value, MEAN, SD);

        assertEquals(ValidationAnalyticsDescriptions.DESCRIPTIONS.get(0), specsValidator.getDescription());
        assertEquals(ThresholdAnalyticsRules.RULES.get(0), specsValidator.getRules());
    }

    @Test
    @DisplayName("Should handle edge case at exactly -1SD")
    void testValueExactlyAtNegative1SD() {
        double value = MEAN - SD; // Exactly at -1SD

        specsValidator.validator(value, MEAN, SD);

        assertEquals(ValidationAnalyticsDescriptions.DESCRIPTIONS.get(3), specsValidator.getDescription());
        assertEquals(ThresholdAnalyticsRules.RULES.get(3), specsValidator.getRules());
    }
}
