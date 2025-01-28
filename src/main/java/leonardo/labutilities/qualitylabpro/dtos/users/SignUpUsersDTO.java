package leonardo.labutilities.qualitylabpro.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignUpUsersDTO(
        @NotNull String identifier,
        @Email
        String email,
        @NotNull @Pattern(
                regexp = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?])(?=.*\\d{4,}).+$",
                message = "Password must contain at least 4 characters and one special character.")
        String password


) {
}
