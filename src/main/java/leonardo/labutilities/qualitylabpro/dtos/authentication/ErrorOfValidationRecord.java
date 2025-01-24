package leonardo.labutilities.qualitylabpro.dtos.authentication;

import org.springframework.validation.FieldError;

public record ErrorOfValidationRecord(String Field, String Message) {
    public ErrorOfValidationRecord(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}
