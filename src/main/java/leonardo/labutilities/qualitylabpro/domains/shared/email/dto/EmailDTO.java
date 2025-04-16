package leonardo.labutilities.qualitylabpro.domains.shared.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data Transfer Object for email sending requests")
public record EmailDTO(
		@Schema(description = "Recipient email address", example = "recipient@example.com",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank @Email String to,

		@Schema(description = "Email subject line", example = "Quality Control Report",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String subject,

		@Schema(description = "Email message content",
				example = "Please find attached the quality control report for today.",
				requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String body) {}
