package leonardo.labutilities.qualitylabpro.domains.shared.exception;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
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

	// ==========================================
	// 400 BAD REQUEST exceptions
	// ==========================================

	@ApiResponses(
			value = {@ApiResponse(responseCode = "400", description = "Invalid input supplied",
					content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler({MethodArgumentNotValidException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

		ApiError apiError =
				ApiError.of(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI())
						.withValidationErrors(errors);

		log.error("400 Bad Request: Validation failed [{}]: {}", request.getRequestURI(), errors);
		return ResponseEntity.badRequest().body(apiError);
	}


	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {
		List<String> details = ex.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
				.toList();

		ApiError apiError =
				ApiError.of(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI())
						.withDetails(details);

		log.error("400 Bad Request: Constraint violation [{}]", request.getRequestURI());
		return ResponseEntity.badRequest().body(apiError);
	}

	// ==========================================
	// 401 UNAUTHORIZED exceptions
	// ==========================================


	@ApiResponses(value = {@ApiResponse(responseCode = "401", description = "Authentication failed",
			content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler({BadCredentialsException.class, PasswordNotMatchesException.class})
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ApiError> handleAuthenticationErrors(Exception ex,
			HttpServletRequest request) {
		String errorMessage = "Invalid credentials or password does not match";

		List<String> details = new ArrayList<>();
		details.add("Please verify your credentials and try again.");

		ApiError apiError =
				ApiError.of(HttpStatus.UNAUTHORIZED, errorMessage, request.getRequestURI())
						.withDetails(details);

		log.error("401 Unauthorized: Auth failed [{}] - {}", request.getRequestURI(),
				ex.getClass().getSimpleName());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
	}

	// ==========================================
	// 403 FORBIDDEN exceptions
	// ==========================================

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

		log.error("403 Forbidden: Invalid token [{}]", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
	}

	@ApiResponses(value = {@ApiResponse(responseCode = "403", description = "Access denied",
			content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex,
			HttpServletRequest request) {
		ApiError apiError =
				ApiError.of(HttpStatus.FORBIDDEN, "Access denied", request.getRequestURI());
		apiError.details().add("You do not have permission to access this resource.");
		apiError.details().add("Please verify your credentials and permissions.");

		log.error("403 Forbidden: Access denied [{}]", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
	}

	// ==========================================
	// 404 NOT FOUND exceptions
	// ==========================================

	@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Resource not found",
			content = @Content(schema = @Schema(implementation = ApiError.class)))})
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex,
			HttpServletRequest request) {
		ApiError apiError =
				ApiError.of(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
		apiError.details().add("The requested resource was not found.");

		log.error("404 Not Found: Resource [{}] - {}", request.getRequestURI(), ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.NOT_FOUND,
				"User not found or invalid credentials", request.getRequestURI());

		apiError.details().add("Invalid credentials or user does not exist.");

		log.error("404 Not Found: User [{}]", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ApiError> handleEndpointNotFound(NoHandlerFoundException ex,
			HttpServletRequest request) {
		ApiError apiError = new ApiError(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
				HttpStatus.NOT_FOUND.getReasonPhrase(), "The requested endpoint was not found",
				request.getRequestURI(), new ArrayList<>());
		apiError.details().add("The endpoint you're trying to access does not exist.");
		apiError.details().add("Please verify the URL and ensure it is correctly formatted.");
		apiError.details()
				.add("If you believe this is a mistake, please contact our support team.");

		log.error("404 Not Found: Endpoint {} [{}]", ex.getHttpMethod(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
	}

	// ==========================================
	// 405 METHOD NOT ALLOWED exceptions
	// ==========================================

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ResponseEntity<ApiError> handleMethodNotAllowed(
			HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
				request.getRequestURI());
		apiError.details().add("The HTTP method used is not supported for this endpoint.");
		apiError.details().add("Allowed methods: " + String.join(", ", ex.getSupportedMethods()));
		apiError.details()
				.add("Please check the API documentation for the correct HTTP method to use.");

		log.error("405 Method Not Allowed: {} [{}]", ex.getMethod(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiError);
	}

	// ==========================================
	// 409 CONFLICT exceptions
	// ==========================================

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

		log.error("409 Conflict: User already exists [{}]", request.getRequestURI());
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

		log.error("409 Conflict: Data integrity violation [{}] - {}", request.getRequestURI(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
	}

	@ExceptionHandler(DuplicateKeyException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ApiError> handleDuplicateKeyException(DuplicateKeyException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.CONFLICT, "Duplicate record exists",
				request.getRequestURI());
		apiError.details().add("A record with the same unique key already exists.");
		apiError.details().add("Please use a different identifier or update the existing record.");

		log.error("409 Conflict: Duplicate key [{}]", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
	}

	// ==========================================
	// 500 INTERNAL SERVER ERROR exceptions
	// ==========================================


	@ExceptionHandler(EmailSendingException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleEmailSendingError(EmailSendingException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email",
				request.getRequestURI());
		apiError.details().add("The system was unable to send the email.");
		apiError.details().add(
				"Please try again later or contact support if the problem persists, sorry for that.");

		String cause = ex.getCause() != null ? ex.getCause().getMessage() : "Unknown";
		log.error("500 Server Error: Email failed [{}] - Cause: {}", request.getRequestURI(),
				cause);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}


	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred", request.getRequestURI());
		apiError.details()
				.add("The system encountered an unexpected error while processing your request.");
		apiError.details().add(
				"Our technical team has been notified and is working to resolve this issue, sorry for that :(.");
		apiError.details()
				.add("Please try again later or contact support if the problem persists.");

		log.error("500 Server Error: Uncaught exception [{}]", request.getRequestURI(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}


	@ExceptionHandler(BadSqlGrammarException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleBadSqlGrammarException(BadSqlGrammarException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Database query error",
				request.getRequestURI());
		apiError.details().add("The system encountered an error while executing a database query.");
		apiError.details().add("Our technical team has been notified.");

		log.error("500 Server Error: SQL grammar issue [{}]", request.getRequestURI(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}


	@ExceptionHandler(QueryTimeoutException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleQueryTimeoutException(QueryTimeoutException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR,
				"Database operation timed out", request.getRequestURI());
		apiError.details().add("The database operation took too long to complete.");
		apiError.details().add("Please try again. If the problem persists, contact support.");

		log.error("500 Server Error: Query timeout [{}]", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}


	@ExceptionHandler(DataRetrievalFailureException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleDataRetrievalFailure(DataRetrievalFailureException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve data",
				request.getRequestURI());
		apiError.details().add("The system was unable to retrieve the requested data.");
		apiError.details()
				.add("Please try again later or contact support if the problem persists.");

		log.error("500 Server Error: Data retrieval failure [{}]", request.getRequestURI(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}


	@ExceptionHandler(DataAccessException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleDataAccessException(DataAccessException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Database access error",
				request.getRequestURI());
		apiError.details().add("An error occurred while accessing the database.");
		apiError.details()
				.add("Our technical team has been notified and is working to resolve this issue.");

		log.error("500 Server Error: Database access issue [{}]: {}", request.getRequestURI(),
				ex.getMostSpecificCause().getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}


	@ExceptionHandler(SQLException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiError> handleSQLException(SQLException ex,
			HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, "Database error",
				request.getRequestURI());
		apiError.details().add("A database error occurred while processing your request.");
		apiError.details()
				.add("Our technical team has been notified and is working to resolve this issue.");

		log.error("500 Server Error: SQL exception [{}] - State: {}, Code: {}",
				request.getRequestURI(), ex.getSQLState(), ex.getErrorCode(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
	}


	@ExceptionHandler(DatabaseConnectionException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public ResponseEntity<ApiError> handleDatabaseConnectionException(
			DatabaseConnectionException ex, HttpServletRequest request) {
		ApiError apiError = ApiError.of(HttpStatus.SERVICE_UNAVAILABLE, "Database connection error",
				request.getRequestURI());
		apiError.details().add("The system is currently unable to connect to the database.");
		apiError.details().add("Please try again later. We apologize for the inconvenience.");

		log.error("503 Service Unavailable: Database connection error [{}]",
				request.getRequestURI(), ex);
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(apiError);
	}

	// ==========================================
	// Exception classes
	// ==========================================


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
			super("Resource not found");
		}

		public ResourceNotFoundException(String message) {
			super(message);
		}
	}


	public static class PasswordNotMatchesException extends RuntimeException {
		public PasswordNotMatchesException() {
			super("Password does not match");
		}
	}


	public static class DataIntegrityViolationException extends RuntimeException {
		public DataIntegrityViolationException() {
			super("Data integrity violation occurred");
		}

		public DataIntegrityViolationException(String message) {
			super(message);
		}
	}


	public static class UserAlreadyExistException extends RuntimeException {
		public UserAlreadyExistException() {
			super("User already exists");
		}

		public UserAlreadyExistException(String message) {
			super(message);
		}
	}


	public static class UserNotFoundException extends RuntimeException {
		public UserNotFoundException() {
			super("User not found - Invalid credentials or user does not exist.");
		}

		public UserNotFoundException(String message) {
			super(message);
		}
	}


	public static class RecoveryTokenInvalidException extends RuntimeException {
		public RecoveryTokenInvalidException() {
			super("Recovery token is invalid or expired.");
		}

		public RecoveryTokenInvalidException(String message) {
			super(message);
		}

		public RecoveryTokenInvalidException(String message, Throwable cause) {
			super(message, cause);
		}
	}


	public static class DatabaseConnectionException extends RuntimeException {
		public DatabaseConnectionException() {
			super("Unable to connect to database");
		}

		public DatabaseConnectionException(String message) {
			super(message);
		}

		public DatabaseConnectionException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
