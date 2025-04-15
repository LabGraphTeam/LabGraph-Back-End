package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import java.util.SortedMap;

public record ControlLotDTO(
        Integer id,
        User createdBy,
        String lotCode,
        SortedMap<Long, Equipment> equipments,
        String manufactureDate,
        String expirationTime) {}
