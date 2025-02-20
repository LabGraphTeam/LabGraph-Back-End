package leonardo.labutilities.qualitylabpro.domains.users.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for user information")
public record UsersDTO(@Schema(description = "Username of the account", example = "johndoe",
                requiredMode = Schema.RequiredMode.REQUIRED) @NotNull String username,

                @Schema(description = "User's email address", example = "john.doe@example.com",
                                requiredMode = Schema.RequiredMode.REQUIRED) @Email String email,

                @Schema(description = "User's password (must contain at least 4 characters and one special character)",
                                example = "pass123!@",
                                requiredMode = Schema.RequiredMode.REQUIRED) String password)

{}
