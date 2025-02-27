package leonardo.labutilities.qualitylabpro.domains.users.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data Transfer Object for user information")
public record UsersDTO(
        @Schema(description = "Username of the account", example = "Leonardo Meireles",
                requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String username,

        @Schema(description = "User's email address", example = "lemeireles55@outlook.com",
                requiredMode = Schema.RequiredMode.REQUIRED) @Email String email,

        @Schema(description = "User's password (must contain at least 6 characters and one special character)",
                example = "pass123!@", requiredMode = Schema.RequiredMode.REQUIRED) String password)

{
}
