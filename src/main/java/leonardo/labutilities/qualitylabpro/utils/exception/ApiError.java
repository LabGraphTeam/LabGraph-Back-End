package leonardo.labutilities.qualitylabpro.utils.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ApiError {
	private final LocalDateTime timestamp;
	private final int status;
	private final String error;
	private final String message;
	private final String path;
	private List<String> details;

	public ApiError(HttpStatus status, String message, String path) {
		this.timestamp = LocalDateTime.now();
		this.status = status.value();
		this.error = status.getReasonPhrase();
		this.message = message;
		this.path = path;
		this.details = new ArrayList<>();
	}

	public void setDetails(List<String> details) {
		this.details = new ArrayList<>(details);
	}

	public void addValidationErrors(Map<String, String> validationErrors) {
		validationErrors
				.forEach((field, errorMessage) -> this.details.add(field + ": " + errorMessage));
	}
}
