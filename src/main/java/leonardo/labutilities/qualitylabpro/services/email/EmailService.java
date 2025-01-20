package leonardo.labutilities.qualitylabpro.services.email;

import leonardo.labutilities.qualitylabpro.dtos.email.EmailRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {
	private final JavaMailSender javaMailSender;
	@Value("${spring.mail.username}")
	private String emailFrom;
	@Async
	public void sendEmail(EmailRecord email) {
		var message = new SimpleMailMessage();
		message.setFrom(emailFrom);
		message.setTo(email.to());
		message.setSubject("LabGraph - " + email.subject());
		message.setText(buildEmailBody(email));
		javaMailSender.send(message);
	}

	private String buildEmailBody(EmailRecord email) {
		return String.format("\n\n%s\n\nBest regards,\nLabGraph Team",
				email.body());
	}
}