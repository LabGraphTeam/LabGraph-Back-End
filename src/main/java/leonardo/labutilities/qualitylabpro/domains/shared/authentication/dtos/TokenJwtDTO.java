package leonardo.labutilities.qualitylabpro.domains.shared.authentication.dtos;

import java.time.Instant;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for JWT authentication token response")
public record TokenJwtDTO(
		@Schema(description = "JWT authentication token",
				example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
				requiredMode = Schema.RequiredMode.REQUIRED) String tokenJWT,

		@Schema(description = "Token expiration date and time",
				example = "2024-12-31T23:59:59.999Z",
				requiredMode = Schema.RequiredMode.REQUIRED) Instant dateExp) {}
