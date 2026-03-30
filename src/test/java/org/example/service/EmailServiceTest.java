package org.example.service;

import org.example.dto.UserEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "siteName", "TestSite");
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@localhost");
    }

    @Test
    void sendUserEventNotification_UserCreated_ShouldSendWelcomeEmail() {
        UserEventDto event = new UserEventDto(UserEventDto.EventType.CREATED, "test@example.com");

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        doNothing().when(mailSender).send(messageCaptor.capture());

        emailService.sendUserEventNotification(event);

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals("Добро пожаловать! Аккаунт успешно создан", sentMessage.getSubject());
        String text = sentMessage.getText();

        assertTrue(text.contains("Здравствуйте!"));
        assertTrue(text.contains("аккаунт на сайте TestSite"));
        assertTrue(text.contains("был успешно создан"));
    }

    @Test
    void sendUserEventNotification_UserDeleted_ShouldSendDeletionEmail() {
        UserEventDto event = new UserEventDto(UserEventDto.EventType.DELETED, "test@example.com");

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        doNothing().when(mailSender).send(messageCaptor.capture());

        emailService.sendUserEventNotification(event);

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals("Ваш аккаунт был удален", sentMessage.getSubject());
        String text = sentMessage.getText();

        assertTrue(text.contains("Здравствуйте!"));
        assertTrue(text.contains("аккаунт на сайте TestSite"));
        assertTrue(text.contains("был удалён") || text.contains("был удален"));
    }

    @Test
    void sendCustomEmail_ShouldSendEmailWithCustomContent() {
        String to = "custom@example.com";
        String subject = "Custom Subject";
        String message = "Custom Message";

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        doNothing().when(mailSender).send(messageCaptor.capture());

        emailService.sendCustomEmail(to, subject, message);

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(message, sentMessage.getText());
    }

    @Test
    void sendUserEventNotification_WithNullEmail_ShouldThrowException() {
        UserEventDto event = new UserEventDto(UserEventDto.EventType.CREATED, null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendUserEventNotification(event);
        });

        assertTrue(exception.getMessage().contains("не может быть пустым") ||
                exception.getMessage().contains("empty"));

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendUserEventNotification_WithEmptyEmail_ShouldThrowException() {
        UserEventDto event = new UserEventDto(UserEventDto.EventType.CREATED, "");

        assertThrows(RuntimeException.class, () -> {
            emailService.sendUserEventNotification(event);
        });

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendCustomEmail_WithNullEmail_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            emailService.sendCustomEmail(null, "Subject", "Message");
        });

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendCustomEmail_WithEmptyEmail_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> {
            emailService.sendCustomEmail("", "Subject", "Message");
        });

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}