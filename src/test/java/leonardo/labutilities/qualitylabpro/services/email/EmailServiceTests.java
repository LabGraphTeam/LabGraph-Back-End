package leonardo.labutilities.qualitylabpro.services.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import jakarta.mail.internet.MimeMessage;
import leonardo.labutilities.qualitylabpro.dtos.email.requests.EmailDTO;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

	@Mock
	private JavaMailSender javaMailSender;

	@InjectMocks
	private EmailService emailService;

	private static final String TEST_EMAIL_FROM = "sender@test.com";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(this.emailService, "emailFrom", TEST_EMAIL_FROM);
	}

	@Test
	void shouldSendHtmlEmailSuccessfully() {
		// Given
		EmailDTO emailDTO = new EmailDTO("test@example.com", "Test Subject", "Test Body");
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(this.javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		// When
		this.emailService.sendHtmlEmailWithoutBcc(emailDTO);

		// Then
		verify(this.javaMailSender).createMimeMessage();
		verify(this.javaMailSender).send(mimeMessage);
	}

	@Test
	void shouldThrowRuntimeExceptionWhenEmailFails() {
		// Given
		EmailDTO emailDTO = new EmailDTO("test@example.com", "Test Subject", "Test Body");
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(this.javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		doThrow(new MailSendException("Email sending failed")).when(this.javaMailSender)
				.send(any(MimeMessage.class));

		// Then
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			this.emailService.sendHtmlEmailWithoutBcc(emailDTO);
		});
		assertEquals("Email sending failed", exception.getMessage());
	}
}
