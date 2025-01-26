package leonardo.labutilities.qualitylabpro.dtos.authentication;

import org.springframework.validation.FieldError;

public record ErrorOfValidationDTO(String Field, String Message) {
    public ErrorOfValidationDTO(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}
