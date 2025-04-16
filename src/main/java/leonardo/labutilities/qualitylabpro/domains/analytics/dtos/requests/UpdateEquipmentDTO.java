package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;

public record UpdateEquipmentDTO(

                String commercialName,
                WorkSectorEnum workSector,
                String serialNumber) {}
