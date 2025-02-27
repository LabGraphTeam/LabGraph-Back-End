package leonardo.labutilities.qualitylabpro.domains.shared.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class CustomGlobalErrorHandling {

	@ApiResponses(
			value = {@ApiResponse(responseCode = "400", description = "Invalid input supplied",
					content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
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
		apiError.details().add("The requested resource was not found.");

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
				: "Invalid credentials or password does not match.";

		List<String> details = new ArrayList<>();
		details.add(errorMessage);

		if (ex instanceof BadCredentialsException) {
			details.add("Invalid username/email or password.");
		} else if (ex instanceof PasswordNotMatchesException) {
			details.add("Password does not match.");
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
		apiError.details().add("The provided recovery token is no longer valid or has expired.");
		apiError.details().add("Please request a new recovery token to proceed.");

		log.error("Recovery token validation failed at {}: {}", request.getRequestURI(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
	}

	@ApiResponses(value = {@ApiResponse(responseCode = "403", description = "Access denied",
			content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<ApiError> handleAccessDenied(HttpServletRequest request) {
		ApiError apiError =
				ApiError.of(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI());
		apiError.details().add("You do not have permission to access this resource.");
		apiError.details().add("Please verify your credentials and permissions.");

		log.error("Access denied for request to {}: Insufficient permissions",
				request.getRequestURI());
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
		apiError.details().add("The username or email address is already registered.");
		apiError.details().add("Please try with different credentials or recover your account.");

		log.error("User registration failed at {}: User already exists", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.CONFLICT, "Data integrity violation",
				request.getRequestURI());
		apiError.details().add("The operation could not be completed due to data constraints.");
		apiError.details().add("Please verify your input and try again.");

		log.error("Data integrity violation at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred", request.getRequestURI());
		apiError.details()
				.add("The system encountered an unexpected error while processing your request.");
		apiError.details()
				.add("Our technical team has been notified and is working to resolve this issue.");
		apiError.details()
				.add("Please try again later or contact support if the problem persists.");

		log.error("Unexpected error occurred at {}: {}", request.getRequestURI(), ex.getMessage(),
				ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.NOT_FOUND,
				"User not found or invalid credentials.", request.getRequestURI());

		apiError.details().add("Sign-in - Invalid credentials or user does not exist.");

		log.error("User not found at {}: {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	@ExceptionHandler(EmailSendingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleEmailSendingError(EmailSendingException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email",
				request.getRequestURI());
		apiError.details().add("The system was unable to send the email.");
		apiError.details()
				.add("Please try again later or contact support if the problem persists.");

		log.error("Email sending failed at {}: {}. Cause: {}", request.getRequestURI(),
				ex.getMessage(),
				ex.getCause() != null ? ex.getCause().getMessage() : "Unknown cause");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(NoHandlerFoundException ex,
			HttpServletRequest request) {
		ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
				HttpStatus.NOT_FOUND.getReasonPhrase(), "The requested resource was not found",
				request.getRequestURI(), new ArrayList<>());
		apiError.details().add("The endpoint you're trying to access does not exist.");
		apiError.details().add("Please verify the URL and ensure it is correctly formatted.");
		apiError.details()
				.add("If you believe this is a mistake, please contact our support team.");

		log.error("No handler found for {} {} at {}", ex.getHttpMethod(), ex.getRequestURL(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiError> handleMethodNotAllowed(
			HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
				request.getRequestURI());
		apiError.details().add("The HTTP method used is not supported for this endpoint.");
		apiError.details().add("Allowed methods: " + String.join(", ", ex.getSupportedMethods()));
		apiError.details()
				.add("Please check the API documentation for the correct HTTP method to use.");

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiError);
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
			super("User not found - Invalid credentials or user does not exist.");
		}
	}

	public static class RecoveryTokenInvalidException extends RuntimeException {
		public RecoveryTokenInvalidException() {
			super("Token invalid - Recovery token is invalid or expired.");
		}

		public RecoveryTokenInvalidException(String message) {
			super(message);
		}

		public RecoveryTokenInvalidException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
