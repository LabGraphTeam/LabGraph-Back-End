package leonardo.labutilities.qualitylabpro.domains.users.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data Transfer Object for password recovery request")
public record ForgotPasswordDTO(
		@Schema(description = "Username of the account", example = "Leonardo Meireles",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String username,

		@Schema(description = "Registered email address for verification",
				example = "lemeireles55@outlook.com",
				requiredMode = Schema.RequiredMode.REQUIRED) @Email String email) {
}
