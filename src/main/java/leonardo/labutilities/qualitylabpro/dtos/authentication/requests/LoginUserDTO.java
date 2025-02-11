package leonardo.labutilities.qualitylabpro.dtos.authentication.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for user login credentials")
public record LoginUserDTO(
		@Schema(description = "User identifier (email or username)",
				example = "john.doe@example.com",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String identifier,

		@Schema(description = "User password", example = "password123@",
				requiredMode = Schema.RequiredMode.REQUIRED,
				minLength = 6) @NotNull String password) {
}
