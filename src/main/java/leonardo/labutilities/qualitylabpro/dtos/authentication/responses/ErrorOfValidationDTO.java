package leonardo.labutilities.qualitylabpro.dtos.authentication.responses;

import org.springframework.validation.FieldError;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for field validation error details")
public record ErrorOfValidationDTO(
        @Schema(description = "Name of the field that failed validation", example = "email",
                requiredMode = Schema.RequiredMode.REQUIRED) String Field,

        @Schema(description = "Validation error message", example = "must not be blank",
                requiredMode = Schema.RequiredMode.REQUIRED) String Message) {
    public ErrorOfValidationDTO(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}
