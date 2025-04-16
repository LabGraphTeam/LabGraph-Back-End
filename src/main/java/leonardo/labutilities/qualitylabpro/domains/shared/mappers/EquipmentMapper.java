package leonardo.labutilities.qualitylabpro.domains.shared.mappers;

import java.util.List;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.EquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;

public class EquipmentMapper {

    private EquipmentMapper() {}

    public static Equipment toEntity(EquipmentDTO equipmentDTO) {

        var controlLots = List.of(new ControlLot());

        Equipment equipment = new Equipment();

        equipment.setCommercialName(equipmentDTO.commercialName());
        equipment.setWorkSector(equipmentDTO.workSector());
        equipment.setSerialNumber(equipmentDTO.serialNumber());
        equipment.setControlLots(controlLots);
        return equipment;
    }

    public static EquipmentDTO toDTO(Equipment equipment) {
        return new EquipmentDTO(
                equipment.getCommercialName(),
                equipment.getWorkSector(),
                equipment.getSerialNumber(),
                getControlLots(equipment));
    }

    private static String getControlLots(Equipment equipment) {
        try {
            return equipment.getControlLots() != null
                    ? equipment.getControlLots().stream()
                            .map(ControlLot::getLotCode)
                            .reduce((first, second) -> first + ", " + second)
                            .orElse("-")
                    : "-";
        } catch (Exception e) {
            throw new IllegalStateException("Error while retrieving the control's lotCode", e);
        }
    }
}
