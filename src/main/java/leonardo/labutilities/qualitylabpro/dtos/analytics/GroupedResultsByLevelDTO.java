package leonardo.labutilities.qualitylabpro.dtos.analytics;

public record GroupedResultsByLevelDTO(GroupedValuesByLevelDTO groupedValuesByLevelDTO,
                                       GroupedMeanAndStdByLevelDTO groupedMeanAndStdByLevelDTO) {
}
