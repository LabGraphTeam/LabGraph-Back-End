package leonardo.labutilities.qualitylabpro.domains.shared.mappers;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.EquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;

public class EquipmentMapper {

    private EquipmentMapper() {}

    public static Equipment mapToEquipment(EquipmentDTO equipmentDTO) {
        return new Equipment(
                equipmentDTO.commercialName(),
                equipmentDTO.workSector(),
                equipmentDTO.serialNumber());
    }
}
