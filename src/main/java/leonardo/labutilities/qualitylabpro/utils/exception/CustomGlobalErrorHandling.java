package leonardo.labutilities.qualitylabpro.utils.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class CustomGlobalErrorHandling {

	@ApiResponses(
			value = {@ApiResponse(responseCode = "400", description = "Invalid input supplied",
					content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

		ApiError apiError =
				ApiError.of(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI())
						.withValidationErrors(errors);

		log.error("Validation failed for request to {}: {}", request.getRequestURI(), errors);
		return ResponseEntity.badRequest().body(apiError);
	}

	@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Resource not found",
			content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex,
			HttpServletRequest request) {
		ApiError apiError =
				ApiError.of(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());

		log.error("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	@ApiResponses(value = {@ApiResponse(responseCode = "401", description = "Authentication failed",
			content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler({BadCredentialsException.class, PasswordNotMatchesException.class})
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ApiError> handleAuthenticationErrors(Exception ex,
			HttpServletRequest request) {
		String errorMessage = (ex.getMessage() != null) ? ex.getMessage()
				: "Invalid credentials or password does not match";

		List<String> details = new ArrayList<>();
		details.add("Authentication failed: " + errorMessage);

		if (ex instanceof BadCredentialsException) {
			details.add("Invalid username/email or password");
		} else if (ex instanceof PasswordNotMatchesException) {
			details.add("Password does not match");
		}

		ApiError apiError =
				ApiError.of(HttpStatus.UNAUTHORIZED, errorMessage, request.getRequestURI())
						.withDetails(details);

		log.error("Authentication failed at {}: {}, Exception type: {}", request.getRequestURI(),
				errorMessage, ex.getClass().getSimpleName());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
	}

	@ApiResponses(
			value = {@ApiResponse(responseCode = "403", description = "Invalid recovery token",
					content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler(RecoveryTokenInvalidException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<ApiError> handleRecoveryTokenInvalid(RecoveryTokenInvalidException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.FORBIDDEN,
				"Recovery token is invalid or expired", request.getRequestURI());

		log.error("Invalid recovery token at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
	}

	@ApiResponses(value = {@ApiResponse(responseCode = "403", description = "Access denied",
			content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<ApiError> handleAccessDenied(HttpServletRequest request) {
		ApiError apiError =
				ApiError.of(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI());

		log.error("Access denied at {}", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
	}

	@ApiResponses(value = {@ApiResponse(responseCode = "409", description = "User already exists",
			content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler(UserAlreadyExistException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ApiError> handleUserAlreadyExist(UserAlreadyExistException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.CONFLICT,
				"A user with this username or email already exists", request.getRequestURI());

		log.error("A user with this username or email already exists at {}: {}",
				request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.CONFLICT, "Data integrity violation",
				request.getRequestURI());

		log.error("Data integrity violation at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred", request.getRequestURI());

		log.error("Unexpected error occurred at {}: {}", request.getRequestURI(), ex.getMessage(),
				ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.NOT_FOUND,
				"User not found or invalid credentials", request.getRequestURI());

		log.error("User not found at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	@ExceptionHandler(EmailSendingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleEmailSendingError(EmailSendingException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email",
				request.getRequestURI());

		log.error("Email sending failed at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}

	// Exception classes
	public static class EmailSendingException extends RuntimeException {
		public EmailSendingException(String message) {
			super(message);
		}

		public EmailSendingException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	public static class ResourceNotFoundException extends RuntimeException {
		public ResourceNotFoundException() {
			super();
		}

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
		public UserAlreadyExistException() {
			super();
		}
	}

	public static class UserNotFoundException extends RuntimeException {
		public UserNotFoundException() {
			super("User not found - Invalid credentials or user does not exist");
		}
	}

	public static class RecoveryTokenInvalidException extends RuntimeException {
		public RecoveryTokenInvalidException() {
			super("Token invalid - Recovery token is invalid or expired");
		}

		public RecoveryTokenInvalidException(String message) {
			super(message);
		}

		public RecoveryTokenInvalidException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
