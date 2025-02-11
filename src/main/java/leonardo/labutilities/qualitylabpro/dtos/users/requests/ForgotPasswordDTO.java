package leonardo.labutilities.qualitylabpro.dtos.users.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for password recovery request")
public record ForgotPasswordDTO(
		@Schema(description = "Username of the account", example = "johndoe",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String username,

		@Schema(description = "Registered email address for verification",
				example = "john.doe@example.com",
				requiredMode = Schema.RequiredMode.REQUIRED) @Email @NotNull String email) {
}
