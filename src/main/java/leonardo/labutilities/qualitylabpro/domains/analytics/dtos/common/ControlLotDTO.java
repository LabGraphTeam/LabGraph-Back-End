package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common;

import java.time.LocalDate;

import org.hibernate.LazyInitializationException;

import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ControlLotDTO(
        Integer id,
        String createdBy,
        String lotCode,
        LocalDate manufactureDate,
        LocalDate expirationTime,
        String equipment) {
    public ControlLotDTO(ControlLot controlLot) {
        this(
                controlLot.getId(),
                controlLot.getUser().getUsername(),
                controlLot.getLotCode(),
                controlLot.getManufactureDate(),
                controlLot.getExpirationTime(),
                safeGetEquipmentName(controlLot));
    }

    private static String safeGetEquipmentName(ControlLot controlLot) {
        if (controlLot.getEquipment() == null) {
            return "Equipmento not found";
        }

        try {
            return controlLot.getEquipment().getCommercialName();
        } catch (LazyInitializationException e) {
            log.debug("Lazy initialization exception when retrieving equipment username: {}",
                    e.getMessage());
            return "Lazy initialization exception";
        } catch (Exception e) {
            log.debug("Error retrieving validator username: {}", e.getMessage());
            return "Error retrieving equipment name";
        }
    }
}
