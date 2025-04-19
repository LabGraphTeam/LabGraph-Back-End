package leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;

/**
 * Data Transfer Object for Equipment entity. Used for creating and updating
 * equipment information.
 */
public record EquipmentDTO(
                @NotBlank(message = "Commercial name is required") @Size(max = 100,
                                message = "Commercial name must be less than 100 characters") String commercialName,

                @NotNull(message = "Work sector is required") WorkSectorEnum workSector,

                @Size(max = 150, message = "Serial number must be less than 150 characters") String serialNumber)

{}
