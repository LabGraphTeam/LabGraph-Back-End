package leonardo.labutilities.qualitylabpro.domains.shared.email;

import static leonardo.labutilities.qualitylabpro.domains.shared.email.constants.EmailTemplate.ANALYTICS_WARNING_HEADER;
import static leonardo.labutilities.qualitylabpro.domains.shared.email.constants.EmailTemplate.EMAIL_SUBJECT_PREFIX;
import static leonardo.labutilities.qualitylabpro.domains.shared.email.constants.EmailTemplate.HTML_TEMPLATE;
import static leonardo.labutilities.qualitylabpro.domains.shared.email.constants.EmailTemplate.LAST_ANALYTICS_PARAGRAPH;
import static leonardo.labutilities.qualitylabpro.domains.shared.email.constants.EmailTemplate.TABLE_ROW;
import static leonardo.labutilities.qualitylabpro.domains.shared.email.constants.EmailTemplate.TABLE_STYLE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.email.dto.EmailDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@EnableAsync
@Service
public class EmailService {
	private final JavaMailSender javaMailSender;

	@Lazy
	@Resource
	private EmailService self;

	@Value("${spring.mail.username}")
	private String emailFrom;

	@Value("${email.to.send.list}")
	private String emailListString;

	private List<String> emailList;

	@PostConstruct
	public void init() {
		this.emailList = (this.emailListString != null && !this.emailListString.isEmpty())
				? List.of(this.emailListString.split(","))
				: List.of();
	}

	@Async
	public void sendPlainTextEmail(EmailDTO email) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(this.emailFrom);
		message.setTo(email.to());
		message.setSubject(EMAIL_SUBJECT_PREFIX + email.subject());
		message.setText(buildEmailBody(email.body()));
		this.javaMailSender.send(message);
	}

	@Async
	public void sendHtmlEmailWithoutBcc(EmailDTO emailDTO) {
		MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(this.emailFrom);

			helper.addTo(emailDTO.to());
			helper.setSubject(EMAIL_SUBJECT_PREFIX + emailDTO.subject());
			helper.setText(buildEmailBody(emailDTO.body()), true);

			this.javaMailSender.send(mimeMessage);

		} catch (MessagingException e) {
			throw new EmailSendingException("Email sending failed", e);
		}
	}

	@Async
	public void sendHtmlEmail(EmailDTO emailDTO) {
		MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(this.emailFrom);

			// Convert identifier list to InternetAddress array
			InternetAddress[] internetAddresses =
					this.emailList.stream().map((String emailAddress) -> {
						try {
							return new InternetAddress(emailAddress);
						} catch (AddressException e) {
							return null;
						}
					}).filter(Objects::nonNull).toArray(InternetAddress[]::new);

			if (internetAddresses.length == 0) {
				return;
			}

			helper.setBcc(internetAddresses);
			helper.setSubject(EMAIL_SUBJECT_PREFIX + emailDTO.subject());
			helper.setText(buildEmailBody(emailDTO.body()), true);

			this.javaMailSender.send(mimeMessage);
			log.info("HTML identifier sent successfully to {} recipients",
					internetAddresses.length);

		} catch (MessagingException e) {
			log.error("Failed to send HTML identifier: {}", e.getMessage(), e);
			throw new EmailSendingException("Email sending failed", e);
		}
	}

	@Async
	public void sendFailedAnalyticsNotification(List<AnalyticsDTO> failedRecords,
			String validationResults) {
		if (failedRecords == null || failedRecords.isEmpty()) {
			return;
		}
		log.info(validationResults);

		String emailBody = this.generateAnalyticsFailedEmailBody(failedRecords, validationResults);
		MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(this.emailFrom);
			InternetAddress[] internetAddresses = this.emailList.stream().map((String email) -> {
				try {
					return new InternetAddress(email);
				} catch (AddressException e) {
					log.error("Invalid identifier address: {}", email, e);
					return null;
				}
			}).filter(Objects::nonNull).toArray(InternetAddress[]::new);

			helper.setBcc(internetAddresses);
			helper.setSubject(EMAIL_SUBJECT_PREFIX + "Quality Control Alert: Failed Analytic");
			helper.setText(buildEmailBody(emailBody), true);
			this.javaMailSender.send(mimeMessage);

		} catch (MessagingException e) {
			throw new EmailSendingException("Failed to send analytics notification", e);
		}
	}

	public String generateAnalyticsFailedEmailBody(List<AnalyticsDTO> notPassedList,
			String otherValidations) {
		String formattedList = notPassedList.stream()
				.map(analytics -> String.format(TABLE_ROW, analytics.name(), analytics.level(),
						analytics.value().toString(), analytics.mean().toString(),
						analytics.rules(), analytics.description(),
						analytics.date().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))
				.collect(Collectors.joining("\n"));
		return String.format(HTML_TEMPLATE,
				ANALYTICS_WARNING_HEADER + String.format(TABLE_STYLE, formattedList)
						+ LAST_ANALYTICS_PARAGRAPH + "\n" + otherValidations);
	}

	public void notifyUserLogin(String username, String email, LocalDateTime date) {
		this.sendUserActionEmail("Successful Login", username, email, date);
	}

	public void notifyFailedUserLogin(String username, String email, LocalDateTime date) {
		this.sendUserActionEmail("Failed Login Attempt", username, email, date);
	}

	public void notifyUserSignup(String username, String email, LocalDateTime date) {
		this.sendUserActionEmail("Account Creation", username, email, date);
	}

	public void notifyUserDeletion(String username, String email, LocalDateTime date) {
		this.sendUserActionEmail("Account Deletion", username, email, date);
	}

	public void notifyUserUpdate(String username, String email, LocalDateTime date) {
		this.sendUserActionEmail("Account Update", username, email, date);
	}

	public void sendUserActionEmail(String actionType, String username, String email,
			LocalDateTime date) {
		String subject = String.format("User %s - %s", username, actionType);
		String content = createUserActionEmailContent(actionType, username, email, date);
		self.sendHtmlEmailWithoutBcc(new EmailDTO(email, subject, content));
	}

	private static String createUserActionEmailContent(String actionType, String username,
			String email, LocalDateTime date) {
		String message = String.format(
				"<p>User <b>%s</b> - %s notification<br>Email: <b>%s</b><br>Time: <b>%s</b></p>",
				username, actionType, email, date);
		return String.format(HTML_TEMPLATE, message);
	}

	private static String buildEmailBody(String email) {
		return String.format("%n%n%s%n%nBest regards,%nLabGraph Team", email);
	}
}
