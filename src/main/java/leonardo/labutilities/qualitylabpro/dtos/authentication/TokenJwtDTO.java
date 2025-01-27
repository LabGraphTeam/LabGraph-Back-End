package leonardo.labutilities.qualitylabpro.dtos.authentication;

import java.time.Instant;

public record TokenJwtDTO(String tokenJWT, Instant dateExp) {
}
