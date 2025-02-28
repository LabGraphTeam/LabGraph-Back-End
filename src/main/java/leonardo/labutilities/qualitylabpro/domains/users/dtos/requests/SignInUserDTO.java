package leonardo.labutilities.qualitylabpro.domains.users.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for user login credentials")
public record SignInUserDTO(
		@Schema(description = "User identifier (email or username)",
				example = "lemeireles55@outlook.com",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String identifier,

		@Schema(description = "User password", example = "password123@",
				requiredMode = Schema.RequiredMode.REQUIRED,
				minLength = 6) @NotNull String password) {
}
