package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;

public record EquipmentDTO(

                String commercialName,
                WorkSectorEnum workSector,
                String serialNumber,
                String Equipments) {}
