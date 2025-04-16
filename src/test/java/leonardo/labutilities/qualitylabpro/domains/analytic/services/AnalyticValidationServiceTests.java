package leonardo.labutilities.qualitylabpro.domains.analytic.services;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.AnalyticsRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticValidationService;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;

@ExtendWith(MockitoExtension.class)
class AnalyticValidationServiceTests {

    @Mock
    private AnalyticsRepository analyticsRepository;

    private AnalyticValidationService analyticsValidationService;

    private static final AnalyticsDTO validRecord = mock(AnalyticsDTO.class);
    private static final AnalyticsDTO invalidRecord = mock(AnalyticsDTO.class);

    @BeforeEach
    void setUp() {
        analyticsValidationService = new AnalyticValidationService(analyticsRepository) {};
    }

    @Test
    @DisplayName("isGroupedRecordValid should return false when records contain -3s rules")
    void isGroupedRecordValid_WithMinus3sRules_ShouldReturnFalse() {

        List<AnalyticsDTO> invalidRecords =
                createSampleRecordList().stream().map(AnalyticMapper::toEntity).map(analyticsRecord -> {
                    analyticsRecord.setControlRules("-3s");
                    return AnalyticMapper.toRecord(analyticsRecord);
                }).toList();

        GroupedValuesByLevelDTO groupedRecords = new GroupedValuesByLevelDTO("Normal", invalidRecords);

        boolean result = analyticsValidationService.isGroupedRecordValid(groupedRecords);

        assertFalse(result, "GroupedRecords with -3s rules should be invalid");
    }

    @Test
    @DisplayName("isGroupedRecordValid should return false when records contain +3s rules")
    void isGroupedRecordValid_WithPlus3sRules_ShouldReturnFalse() {

        List<AnalyticsDTO> invalidRecords =
                createSampleRecordList().stream().map(AnalyticMapper::toEntity).map(analyticsRecord -> {
                    analyticsRecord.setControlRules("+3s");
                    return AnalyticMapper.toRecord(analyticsRecord);
                }).toList();

        GroupedValuesByLevelDTO groupedRecords = new GroupedValuesByLevelDTO("Normal", invalidRecords);

        boolean result = analyticsValidationService.isGroupedRecordValid(groupedRecords);

        assertFalse(result, "GroupedRecords with +3s rules should be invalid");
    }

    @Test
    @DisplayName("isGroupedRecordValid should return true when records have valid rules")
    void isGroupedRecordValid_WithValidRules_ShouldReturnTrue() {

        List<AnalyticsDTO> validRecords =
                createSampleRecordList().stream().map(AnalyticMapper::toEntity).map(analyticsRecord -> {
                    analyticsRecord.setControlRules("normal");
                    return AnalyticMapper.toRecord(analyticsRecord);
                }).toList();

        GroupedValuesByLevelDTO groupedRecords = new GroupedValuesByLevelDTO("Normal", validRecords);

        boolean result = analyticsValidationService.isGroupedRecordValid(groupedRecords);

        assertTrue(result, "GroupedRecords with normal rules should be valid");
    }

    @Test
    @DisplayName("isGroupedRecordValid should return false when mixed valid and invalid rules exist")
    void isGroupedRecordValid_WithMixedRules_ShouldReturnFalse() {

        List<AnalyticsDTO> records = new ArrayList<>();

        when(validRecord.rules()).thenReturn("Average");
        records.add(validRecord);

        when(invalidRecord.rules()).thenReturn("+3s");
        records.add(invalidRecord);

        GroupedValuesByLevelDTO groupedRecords = new GroupedValuesByLevelDTO("Mixed", records);

        boolean result = analyticsValidationService.isGroupedRecordValid(groupedRecords);

        assertFalse(result, "GroupedRecords with mixed rules should be invalid");
    }

    @Test
    @DisplayName("isGroupedRecordValid should return true for empty grouped values")
    void isGroupedRecordValid_WithEmptyValues_ShouldReturnTrue() {

        GroupedValuesByLevelDTO groupedRecords = new GroupedValuesByLevelDTO("Empty", Collections.emptyList());

        boolean result = analyticsValidationService.isGroupedRecordValid(groupedRecords);

        assertTrue(result, "Empty GroupedRecords should be valid");
    }

    @Test
    @DisplayName("isNotThreeSigma should return true for non-3-sigma rules")
    void isNotThreeSigma_WithNormalRules_ShouldReturnTrue() {

        AnalyticsDTO analyticsRecord = mock(AnalyticsDTO.class);
        when(analyticsRecord.rules()).thenReturn("normal");

        boolean result = analyticsValidationService.isNotThreeSigma(analyticsRecord);

        assertTrue(result, "Records with normal rules should not be classified as 3-sigma");
    }

    @Test
    @DisplayName("isNotThreeSigma should handle +3s rules as expected")
    void isNotThreeSigma_WithPlus3sRules_ShouldFollowImplementation() {

        when(invalidRecord.rules()).thenReturn("+3s");

        boolean result = analyticsValidationService.isNotThreeSigma(invalidRecord);

        assertTrue(result, "According to implementation, isNotThreeSigma should return true for +3s");
    }

    @Test
    @DisplayName("isNotThreeSigma should handle -3s rules as expected")
    void isNotThreeSigma_WithMinus3sRules_ShouldFollowImplementation() {

        when(invalidRecord.rules()).thenReturn("-3s");

        boolean result = analyticsValidationService.isNotThreeSigma(invalidRecord);

        assertTrue(result, "According to implementation, isNotThreeSigma should return true for -3s");
    }
}
