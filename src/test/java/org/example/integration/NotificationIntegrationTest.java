package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.NotificationRequestDto;
import org.example.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.test-connection=false"
})
class NotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @Test
    void sendCustomNotification_ValidRequest_ShouldReturnSuccess() throws Exception {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setEmail("user@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message Body");

        doNothing().when(emailService).sendCustomEmail(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Email отправлен на user@example.com"));

        verify(emailService, times(1)).sendCustomEmail("user@example.com", "Test Subject", "Test Message Body");
    }

    @Test
    void sendCustomNotification_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setEmail("invalid-email");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendCustomEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendCustomNotification_WithEmptyEmail_ShouldReturnBadRequest() throws Exception {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setEmail("");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendCustomEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendCustomNotification_WithoutSubject_ShouldReturnBadRequest() throws Exception {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setEmail("user@example.com");
        request.setSubject("");
        request.setMessage("Test Message");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendCustomEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendCustomNotification_WithoutMessage_ShouldReturnBadRequest() throws Exception {
        NotificationRequestDto request = new NotificationRequestDto();
        request.setEmail("user@example.com");
        request.setSubject("Test Subject");
        request.setMessage("");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendCustomEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testUserCreatedEndpoint_WithValidEmail_ShouldReturnSuccess() throws Exception {
        doNothing().when(emailService).sendUserEventNotification(any());

        mockMvc.perform(post("/api/notifications/test/user-created")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Тестовое уведомление о создании пользователя отправлено на test@example.com"));

        verify(emailService, times(1)).sendUserEventNotification(any());
    }

    @Test
    void testUserDeletedEndpoint_WithValidEmail_ShouldReturnSuccess() throws Exception {
        doNothing().when(emailService).sendUserEventNotification(any());

        mockMvc.perform(post("/api/notifications/test/user-deleted")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Тестовое уведомление об удалении пользователя отправлено на test@example.com"));

        verify(emailService, times(1)).sendUserEventNotification(any());
    }

    @Test
    void testUserCreatedEndpoint_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/test/user-created")
                        .param("email", "invalid"))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendUserEventNotification(any());
    }

    @Test
    void testUserDeletedEndpoint_WithEmptyEmail_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/test/user-deleted")
                        .param("email", ""))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendUserEventNotification(any());
    }

    @Test
    void testUserCreatedEndpoint_WithoutEmail_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/test/user-created"))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendUserEventNotification(any());
    }

    @Test
    void testUserDeletedEndpoint_WithoutEmail_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/notifications/test/user-deleted"))
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendUserEventNotification(any());
    }
}