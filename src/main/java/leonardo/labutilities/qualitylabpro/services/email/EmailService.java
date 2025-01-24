package leonardo.labutilities.qualitylabpro.services.email;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;
import leonardo.labutilities.qualitylabpro.dtos.email.EmailRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static leonardo.labutilities.qualitylabpro.utils.constants.EmailTemplate.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {
	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	String emailFrom;

	@Value("${email.to.send.list}")
	String emailListString;


	private List<String> emailList;

	@PostConstruct
	private void init() {
		emailList = (emailListString != null && !emailListString.isEmpty())
				? List.of(emailListString.split(","))
				: List.of();

		if (emailList.isEmpty()) {
			log.warn("No email recipients configured in email.to.send.list");
		}
	}

	@Async
	public void sendPlainTextEmail(EmailRecord email) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(emailFrom);
		message.setTo(email.to());
		message.setSubject(EMAIL_SUBJECT_PREFIX + email.subject());
		message.setText(buildEmailBody(email.body()));
		javaMailSender.send(message);
	}

	@Async
	public void sendHtmlEmail(EmailRecord emailRecord) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(emailFrom);

			// Convert email list to InternetAddress array
			InternetAddress[] internetAddresses = emailList.stream().map(emailAddress -> {
				try {
					return new InternetAddress(emailAddress);
				} catch (AddressException e) {
					log.error("Invalid email address: {}", emailAddress, e);
					return null;
				}
			}).filter(Objects::nonNull).toArray(InternetAddress[]::new);

			if (internetAddresses.length == 0) {
				log.error("No valid email addresses found");
				return;
			}

			helper.setBcc(internetAddresses);
			helper.setSubject(EMAIL_SUBJECT_PREFIX + emailRecord.subject());
			helper.setText(buildEmailBody(emailRecord.body()), true);

			javaMailSender.send(mimeMessage);
			log.info("HTML email sent successfully to {} recipients", internetAddresses.length);

		} catch (MessagingException e) {
			log.error("Failed to send HTML email: {}", e.getMessage(), e);
			throw new RuntimeException("Email sending failed", e);
		}
	}

	public void sendFailedAnalyticsNotification(List<AnalyticsRecord> failedRecords,
			String validationResults) {
		if (failedRecords == null || failedRecords.isEmpty()) {
			log.warn("No failed analytics records to send notification for");
			return;
		}
		log.info(validationResults.toString());


		String emailBody = generateAnalyticsFailedEmailBody(failedRecords, validationResults);
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(emailFrom);
			InternetAddress[] internetAddresses = emailList.stream().map(email -> {
				try {
					return new InternetAddress(email);
				} catch (AddressException e) {
					log.error("Invalid email address: {}", email, e);
					return null;
				}
			}).filter(Objects::nonNull).toArray(InternetAddress[]::new);

			helper.setBcc(internetAddresses);
			helper.setSubject(EMAIL_SUBJECT_PREFIX + "Quality Control Alert: Failed Analytics");
			helper.setText(buildEmailBody(emailBody), true);
			javaMailSender.send(mimeMessage);

			log.info("Failed analytics notification sent for {} records", failedRecords.size());
		} catch (MessagingException e) {
			log.error("Failed to send analytics notification email", e);
			throw new RuntimeException("Failed to send analytics notification", e);
		}
	}

	public String generateAnalyticsFailedEmailBody(List<AnalyticsRecord> notPassedList,
			String otherValidations) {
		String formattedList = notPassedList.stream()
				.map(record -> String.format(
						"<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td"
								+ ">%s</td></tr>",
						record.name(), record.level(), record.value().toString(),
						record.mean().toString(), record.rules(),
						record.date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd " + "HH:mm"))))
				.collect(Collectors.joining("\n"));
		return String.format(HTML_TEMPLATE,
				TABLE_STYLE + ANALYTICS_WARNING_HEADER + FAILED_ANALYTICS_HEADER + formattedList
						+ LAST_ANALYTICS_PARAGRAPH + "\n" + otherValidations);
	}

	public void notifyUserLogin(String username, String email, LocalDateTime date) {
		sendUserActionEmail("Successful Login", username, email, date);
	}

	public void notifyFailedUserLogin(String username, String email, LocalDateTime date) {
		sendUserActionEmail("Failed Login Attempt", username, email, date);
	}

	public void notifyUserSignup(String username, String email, LocalDateTime date) {
		sendUserActionEmail("Account Creation", username, email, date);
	}

	public void notifyUserDeletion(String username, String email, LocalDateTime date) {
		sendUserActionEmail("Account Deletion", username, email, date);
	}

	public void notifyUserUpdate(String username, String email, LocalDateTime date) {
		sendUserActionEmail("Account Update", username, email, date);
	}

	private void sendUserActionEmail(String actionType, String username, String email,
			LocalDateTime date) {
		String subject = String.format("User %s - %s", username, actionType);
		String content = createUserActionEmailContent(actionType, username, email, date);
		sendHtmlEmail(new EmailRecord(email, subject, content));
	}

	private String createUserActionEmailContent(String actionType, String username, String email,
			LocalDateTime date) {
		String message = String.format(
				"<p>User <b>%s</b> - %s notification<br>Email: <b>%s</b><br>Time: <b>%s</b></p>",
				username, actionType, email, date);
		return String.format(HTML_TEMPLATE, message);
	}

	private String buildEmailBody(String email) {
		return String.format("\n\n%s\n\nBest regards,\nLabGraph Team", email);
	}
}
