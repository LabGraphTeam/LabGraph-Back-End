package leonardo.labutilities.qualitylabpro.utils.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class CustomGlobalErrorHandling extends RuntimeException {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex,
															   HttpServletRequest request) {
		Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField,
						error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "invalid args"));

		ApiError apiError =
				new ApiError(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI());
		apiError.addValidationErrors(errors);

		log.error("Validation failed for request to {}: {}", request.getRequestURI(), errors);
		return ResponseEntity.badRequest().body(apiError);
	}

	@ExceptionHandler({ResourceNotFoundException.class})
	public ResponseEntity<ApiError> handleNotFound(Exception ex, HttpServletRequest request) {
		ApiError apiError =
				new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());

		log.error("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	@ExceptionHandler({BadCredentialsException.class, PasswordNotMatchesException.class})
	public ResponseEntity<ApiError> handleAuthenticationErrors(Exception ex,
			HttpServletRequest request) {
		ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Authentication failed",
				request.getRequestURI());

		log.error("Authentication failed at {}", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> handleAccessDenied(HttpServletRequest request) {
		ApiError apiError =
				new ApiError(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI());

		log.error("Access denied at {}", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
	}

	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<ApiError> handleUserAllReadyExist(UserAlreadyExistException ex, HttpServletRequest request) {
		ApiError apiError = new ApiError (HttpStatus.BAD_REQUEST, "Username or Email are invalids", request.getRequestURI());

		log.error("Username or Email are invalids at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		ApiError apiError = new ApiError(HttpStatus.CONFLICT,
				"Data integrity violation - the value already exists", request.getRequestURI());

		log.error("Data integrity violation at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex,
			HttpServletRequest request) {
		ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred", request.getRequestURI());

		log.error("Unexpected error occurred at {}: {}", request.getRequestURI(), ex.getMessage(),
				ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}

	// Exception classes
	public static class ResourceNotFoundException extends RuntimeException {
		public ResourceNotFoundException(String message) {
			super(message);
		}
	}

	public static class PasswordNotMatchesException extends RuntimeException {
		public PasswordNotMatchesException() {
			super();
		}
	}

	public static class DataIntegrityViolationException extends RuntimeException {
		public DataIntegrityViolationException() {
			super();
		}
	}

	public static class UserAlreadyExistException extends RuntimeException {
		public UserAlreadyExistException() { super(); }
	}
}
