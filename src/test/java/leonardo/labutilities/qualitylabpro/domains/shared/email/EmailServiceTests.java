package leonardo.labutilities.qualitylabpro.domains.shared.email;

import static leonardo.labutilities.qualitylabpro.utils.AnalyticsHelperMocks.createSampleRecordList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.time.LocalDateTime;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;


import jakarta.mail.internet.MimeMessage;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.common.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.email.dto.EmailDTO;

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
		ReflectionTestUtils.setField(this.emailService, "self", this.emailService);
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
		RuntimeException exception =
				assertThrows(RuntimeException.class, () -> this.emailService.sendHtmlEmailWithoutBcc(emailDTO));
		assertEquals("Email sending failed", exception.getMessage());
	}

	@Test
	void shouldSendPlainTextEmailSuccessfully() {
		// Given
		EmailDTO emailDTO = new EmailDTO("test@example.com", "Test Subject", "Test Body");

		// When
		this.emailService.sendPlainTextEmail(emailDTO);

		// Then
		verify(this.javaMailSender).send(any(SimpleMailMessage.class));
	}

	@Test
	void shouldSendHtmlEmailWithBccSuccessfully() {
		// Given
		EmailDTO emailDTO = new EmailDTO("test@example.com", "Test Subject", "Test Body");
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(this.javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		ReflectionTestUtils.setField(this.emailService, "emailListString",
				"bcc1@test.com,bcc2@test.com");
		this.emailService.init();

		// When
		this.emailService.sendHtmlEmail(emailDTO);

		// Then
		verify(this.javaMailSender).send(mimeMessage);
	}

	@Test
	void shouldSendFailedAnalyticsNotificationSuccessfully() {
		// Given

		List<AnalyticsDTO> failedRecords = createSampleRecordList();

		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(this.javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		ReflectionTestUtils.setField(this.emailService, "emailListString", "test@example.com");
		this.emailService.init();
		// When
		this.emailService.sendFailedAnalyticsNotification(failedRecords, "Validation Results");

		// Then
		verify(this.javaMailSender).send(mimeMessage);
	}

	@Test
	void shouldSendUserActionNotificationsSuccessfully() {
		// Given
		String username = "testuser";
		String email = "test@example.com";
		LocalDateTime date = LocalDateTime.now();
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(this.javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		// When
		this.emailService.notifyUserLogin(username, email, date);
		this.emailService.notifyUserSignup(username, email, date);
		this.emailService.notifyUserDeletion(username, email, date);
		this.emailService.notifyUserUpdate(username, email, date);

		// Then
		verify(this.javaMailSender, times(4)).send(any(MimeMessage.class));
	}

	@Test
	void shouldGenerateCorrectAnalyticsEmailBody() {
		// Given
		List<AnalyticsDTO> failedRecords = createSampleRecordList();
		// When
		String emailBody = this.emailService.generateAnalyticsFailedEmailBody(failedRecords,
				"Test Validation");

		// Then
		assertTrue(emailBody.contains("ALB2"));
		assertTrue(emailBody.contains("Approved"));
	}

	@Test
	void shouldHandleEmptyAnalyticsList() {
		// When
		this.emailService.sendFailedAnalyticsNotification(List.of(), "No failures");

		// Then
		verify(this.javaMailSender, never()).send(any(MimeMessage.class));
	}

	@Test
	void shouldHandleInvalidBccAddresses() {
		// Given
		EmailDTO emailDTO = new EmailDTO("test@example.com", "Test Subject", "Test Body");
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(this.javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		ReflectionTestUtils.setField(this.emailService, "emailListString", "invalid-email");
		this.emailService.init();

		// When & Then
		assertDoesNotThrow(() -> this.emailService.sendHtmlEmail(emailDTO));
	}

	@Test
	void shouldHandleMailSendException() {
		// Given
		EmailDTO emailDTO = new EmailDTO("test@example.com", "Test Subject", "Test Body");
		when(this.javaMailSender.createMimeMessage())
				.thenThrow(new MailSendException("Failed to send"));

		// When & Then
		assertThrows(MailSendException.class,
				() -> this.emailService.sendHtmlEmailWithoutBcc(emailDTO));
	}
}
