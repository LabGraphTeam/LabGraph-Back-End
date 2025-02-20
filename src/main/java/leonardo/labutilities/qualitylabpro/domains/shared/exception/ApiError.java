package leonardo.labutilities.qualitylabpro.domains.shared.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API error response format")
public record ApiError(
		@Schema(description = "Timestamp when the error occurred",
				example = "2024-02-13T10:30:00") LocalDateTime timestamp,

		@Schema(description = "HTTP status code", example = "400") int status,

		@Schema(description = "HTTP error reason", example = "Bad Request") String error,

		@Schema(description = "Detailed error message",
				example = "Invalid input parameters") String message,

		@Schema(description = "The path where the error occurred",
				example = "/api/analytics") String path,

		@Schema(description = "List of detailed error messages",
				example = "[\"field: validation error message\"]") List<String> details) {
	public static ApiError of(HttpStatus status, String message, String path) {
		return new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message,
				path, new ArrayList<>());
	}

	public ApiError withDetails(List<String> newDetails) {
		return new ApiError(this.timestamp, this.status, this.error, this.message, this.path,
				new ArrayList<>(newDetails));
	}

	public ApiError withValidationErrors(Map<String, String> validationErrors) {
		List<String> newDetails = new ArrayList<>(this.details);
		validationErrors
				.forEach((field, errorMessage) -> newDetails.add(field + ": " + errorMessage));
		return new ApiError(this.timestamp, this.status, this.error, this.message, this.path,
				newDetails);
	}
}
