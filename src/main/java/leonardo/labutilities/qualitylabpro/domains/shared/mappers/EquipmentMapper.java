package leonardo.labutilities.qualitylabpro.domains.shared.mappers;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.CreateEquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;

public class EquipmentMapper {

    private EquipmentMapper() {}

    public static Equipment mapToEquipment(CreateEquipmentDTO createEquipmentDTO) {
        return new Equipment(
                createEquipmentDTO.commercialName(),
                createEquipmentDTO.workSector(),
                createEquipmentDTO.serialNumber());
    }
}
