package leonardo.labutilities.qualitylabpro.domains.users.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Data Transfer Object for user registration")
public record SignUpUsersDTO(
        @Schema(description = "User's identifier/username", example = "Leonardo Meireles",
                requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String identifier,

        @Schema(description = "User's email address", example = "lemeireles55@outlook.com",
                requiredMode = Schema.RequiredMode.REQUIRED) @Email String email,

        @Schema(description = "User's password (must contain at least 6 characters and one special character)",
                example = "pass123!@", requiredMode = Schema.RequiredMode.REQUIRED,
                pattern = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?])(?=.*\\d).{6,}$",
                minLength = 6) @NotBlank @Pattern(
                        regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?])(?=.*\\d).{6,}$",
                        message = "Password must contain at least 6 characters and one special character.") String password) {
}
