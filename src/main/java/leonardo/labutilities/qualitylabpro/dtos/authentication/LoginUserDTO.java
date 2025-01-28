package leonardo.labutilities.qualitylabpro.dtos.authentication;

import jakarta.validation.constraints.NotNull;

public record LoginUserDTO(@NotNull String identifier,
                           @NotNull String password) {
}
