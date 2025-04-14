package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;

public record CreateEquipmentDTO(

        String commercialName,
        WorkSectorEnum workSector,
        String serialNumber) {}
