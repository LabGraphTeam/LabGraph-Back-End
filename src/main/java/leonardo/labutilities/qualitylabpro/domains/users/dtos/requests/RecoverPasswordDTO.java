package leonardo.labutilities.qualitylabpro.domains.users.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Data Transfer Object for password recovery process")
public record RecoverPasswordDTO(
		@Schema(description = "User's email address", example = "user@example.com",
				requiredMode = Schema.RequiredMode.REQUIRED) @Email String email,

		@Schema(description = "Temporary password received via email", example = "temp123!@",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String temporaryPassword,

		@Schema(description = "New password (must contain at least 6 characters and one special character)",
				example = "newPass123!@", requiredMode = Schema.RequiredMode.REQUIRED,
				pattern = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?])(?=.*\\d).{6,}$",
				minLength = 6) @NotNull @Pattern(
						regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?])(?=.*\\d).{6,}$",
						message = "Password must contain at least 6 characters and one special character.") String newPassword) {}
