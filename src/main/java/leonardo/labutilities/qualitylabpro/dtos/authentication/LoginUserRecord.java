package leonardo.labutilities.qualitylabpro.dtos.authentication;

import jakarta.validation.constraints.NotNull;

public record LoginUserRecord(@NotNull String email,
                              @NotNull String password) {
}
