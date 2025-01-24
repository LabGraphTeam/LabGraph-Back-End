package leonardo.labutilities.qualitylabpro.utils.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        this.details = details;
    }

    public void addValidationErrors(Map<String, String> validationErrors) {
        validationErrors.forEach((field, message) -> this.details.add(field + ": " + message));
    }
}
