package leonardo.labutilities.qualitylabpro.domains.shared.mappers;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.EquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;

/**
 * Mapper for converting between Equipment entity and EquipmentDTO.
 */
public class EquipmentMapper {

    private EquipmentMapper() {}

    /**
     * Converts EquipmentDTO to Equipment entity.
     *
     * @param equipmentDTO The DTO to convert
     * @return The converted Equipment entity
     */
    public static Equipment toEntity(EquipmentDTO equipmentDTO) {
        if (equipmentDTO == null) {
            return null;
        }

        Equipment equipment = new Equipment();
        equipment.setCommercialName(equipmentDTO.commercialName());
        equipment.setWorkSector(equipmentDTO.workSector());
        equipment.setSerialNumber(equipmentDTO.serialNumber());

        return equipment;
    }

    /**
     * Converts Equipment entity to EquipmentDTO.
     *
     * @param equipment The entity to convert
     * @return The converted EquipmentDTO
     */
    public static EquipmentDTO toDTO(Equipment equipment) {
        if (equipment == null) {
            return null;
        }

        return new EquipmentDTO(
                equipment.getCommercialName(),
                equipment.getWorkSector(),
                equipment.getSerialNumber());
    }
}
