package leonardo.labutilities.qualitylabpro.dtos.email;

import jakarta.validation.constraints.Email;

public record RecoveryEmailDTO(@Email String email, String temporaryPassword) {
}
