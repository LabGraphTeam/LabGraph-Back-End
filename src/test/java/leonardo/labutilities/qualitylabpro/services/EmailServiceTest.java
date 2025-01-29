package leonardo.labutilities.qualitylabpro.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import leonardo.labutilities.qualitylabpro.dtos.email.EmailDTO;
import leonardo.labutilities.qualitylabpro.services.email.EmailService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@Mock
	private JavaMailSender javaMailSender;

	@InjectMocks
	private EmailService emailService;

	private static final String TEST_EMAIL_FROM = "sender@test.com";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(emailService, "emailFrom", TEST_EMAIL_FROM);
	}

	@Test
	void shouldSendHtmlEmailSuccessfully() throws MessagingException {
		// Given
		EmailDTO emailDTO = new EmailDTO("test@example.com", "Test Subject", "Test Body");
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		// When
		emailService.sendHtmlEmailWithoutBcc(emailDTO);

		// Then
		verify(javaMailSender).createMimeMessage();
		verify(javaMailSender).send(mimeMessage);
	}

	@Test
	void shouldThrowRuntimeExceptionWhenEmailFails() throws MessagingException {
		// Given
		EmailDTO emailDTO = new EmailDTO("test@example.com", "Test Subject", "Test Body");
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		doThrow(new MailSendException("Email sending failed")).when(javaMailSender)
				.send(any(MimeMessage.class));

		// Then
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			emailService.sendHtmlEmailWithoutBcc(emailDTO);
		});
		assertEquals("Email sending failed", exception.getMessage());
	}
}
