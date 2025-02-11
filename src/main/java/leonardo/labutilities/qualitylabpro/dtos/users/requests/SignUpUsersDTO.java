package leonardo.labutilities.qualitylabpro.dtos.users.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Data Transfer Object for user registration")
public record SignUpUsersDTO(@Schema(description = "User's identifier/username",
                example = "johndoe",
                requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String identifier,

                @Schema(description = "User's email address", example = "john.doe@example.com",
                                requiredMode = Schema.RequiredMode.REQUIRED) @Email String email,

                @Schema(description = "User's password (must contain at least 6 characters and one special character)",
                                example = "pass123!@", requiredMode = Schema.RequiredMode.REQUIRED,
                                pattern = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?])(?=.*\\d).{6,}$",
                                minLength = 6) @NotNull @Pattern(
                                                regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?])(?=.*\\d).{6,}$",
                                                message = "Password must contain at least 6 characters and one special character.") String password) {
}
