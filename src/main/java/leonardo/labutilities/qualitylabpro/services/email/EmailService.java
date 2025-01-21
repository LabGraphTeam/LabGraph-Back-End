package leonardo.labutilities.qualitylabpro.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;
import leonardo.labutilities.qualitylabpro.dtos.email.EmailRecord;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {
	private static final String EMAIL_SUBJECT_PREFIX = "LabGraph - ";
	private static final String HTML_TEMPLATE = "<html><head></head><body>%s</body></html>";
	private static final String TABLE_STYLE = "<style>table { border-collapse: collapse; width: 100%%; } th, td { border: 1px solid black; padding: 8px; text-align: left; } th { background-color: #f2f2f2; } tr:nth-child(even) { background-color: #f9f9f9; }</style>";

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String emailFrom;

	/**
	 * Sends a plain text email.
	 *
	 * @param email the email record containing recipient, subject, and body
	 */
	@Async
	public void sendPlainTextEmail(EmailRecord email) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(emailFrom);
		message.setTo(email.to());
		message.setSubject(EMAIL_SUBJECT_PREFIX + email.subject());
		message.setText(buildEmailBody(email));
		javaMailSender.send(message);
	}

	/**
	 * Sends an email with an HTML body.
	 *
	 * @param email the email record containing recipient, subject, and body
	 */
	@Async
	public void sendHtmlEmail(EmailRecord email) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(emailFrom);
			helper.setTo(email.to());
			helper.setSubject(EMAIL_SUBJECT_PREFIX + email.subject());
			helper.setText(buildEmailBody(email), true); // true indicates HTML
			javaMailSender.send(mimeMessage);
		} catch (MessagingException e) {
			log.error("Failed to send email with HTML body", e);
		}
	}

	/**
	 * Generates the email body for analytics records that failed.
	 *
	 * @param notPassedList the list of analytics records that did not pass
	 * @return the generated HTML email body
	 */
	public String generateAnalyticsFailedEmailBody(List<AnalyticsRecord> notPassedList) {
		String formattedList = notPassedList.stream()
				.map(record -> String.format(
						"<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
						record.name(), record.level(), record.value().toString(), record.mean().toString(), record.rules(),
						record.description(), record.date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))
				.collect(Collectors.joining("\n"));
		return String.format(HTML_TEMPLATE,
				TABLE_STYLE +
						"<p>The following analytics records did not pass the standard deviation criteria:</p>" +
						"<table><tr><th>Name</th><th>Level</th><th>Value</th><th>Expected Value</th><th>Rules</th><th>Status</th><th>Date</th></tr>" +
						formattedList +
						"</table><p>Please take the necessary actions to address these issues.</p>");
	}

	public String generateUserLoginEmailBody(String username, String email, String date) {
		return generateUserActionEmailBody("logged in", username, email, date);
	}

	public String generateUserCreationEmailBody(String username, String email, String date) {
		return generateUserActionEmailBody("been created", username, email, date);
	}

	public String generateUserDeletionEmailBody(String username, String email, String date) {
		return generateUserActionEmailBody("been deleted", username, email, date);
	}

	public String generateUserUpdateEmailBody(String username, String email, String date) {
		return generateUserActionEmailBody("been updated", username, email, date);
	}

	private String generateUserActionEmailBody(String action, String username, String email, String date) {
		return String.format(HTML_TEMPLATE,
				String.format("<p>User <b>%s</b> has %s with email <b>%s</b> at <b>%s</b>.</p>",
						username, action, email, date));
	}

	private String buildEmailBody(EmailRecord email) {
		return String.format("\n\n%s\n\nBest regards,\nLabGraph Team", email.body());
	}
}