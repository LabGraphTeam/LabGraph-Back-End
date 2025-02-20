package leonardo.labutilities.qualitylabpro.domains.shared.email.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data Transfer Object for password recovery email requests")
public record RecoveryEmailDTO(
		@Schema(description = "User's email address for password recovery",
				example = "user@example.com",
				requiredMode = Schema.RequiredMode.REQUIRED) @Email @NotBlank String email,

		@Schema(description = "Temporary password generated for recovery", example = "temp123!@#",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String temporaryPassword) {}
