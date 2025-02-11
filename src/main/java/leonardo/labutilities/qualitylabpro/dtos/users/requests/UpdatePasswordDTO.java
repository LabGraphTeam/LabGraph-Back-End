package leonardo.labutilities.qualitylabpro.dtos.users.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Data Transfer Object for password update request")
public record UpdatePasswordDTO(
		@Schema(description = "Username of the account", example = "johndoe",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String username,

		@Schema(description = "User's email address for verification",
				example = "john.doe@example.com",
				requiredMode = Schema.RequiredMode.REQUIRED) @Email String email,

		@Schema(description = "Current password", example = "currentPass123!",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String oldPassword,

		@Schema(description = "New password (must contain at least 6 characters and one special character)",
				example = "newPass456@", requiredMode = Schema.RequiredMode.REQUIRED) @Pattern(
						regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?])(?=.*\\d{6,}).+$",
						message = "Password must contain at least 6 characters and one special character.") String newPassword) {
}
