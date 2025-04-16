package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import java.time.LocalDate;

import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;

public record ControlLotDTO(
                Integer id,
                String createdBy,
                String lotCode,
                LocalDate manufactureDate,
                LocalDate expirationTime,
                Integer equipmentId) {
        public ControlLotDTO(ControlLot controlLot) {
                this(
                                controlLot.getId().intValue(),
                                controlLot.getUser().getUsername(),
                                controlLot.getLotCode(),
                                controlLot.getManufactureDate(),
                                controlLot.getExpirationTime(),
                                controlLot.getEquipmentId().getId());
        }
}
