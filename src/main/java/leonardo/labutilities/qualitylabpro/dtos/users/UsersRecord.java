package leonardo.labutilities.qualitylabpro.dtos.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UsersRecord(@NotNull String username, @NotNull @Pattern(
        regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?])(?=.*\\d{4,}).+$",
        message = "Password must contain at least 4 characters and one special character.") String password,
                          String email) {
}
