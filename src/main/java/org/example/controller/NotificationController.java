package org.example.controller;

import org.example.dto.NotificationRequestDto;
import org.example.dto.UserEventDto;
import org.example.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private final EmailService emailService;

    @Autowired
    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendCustomNotification(
            @Valid @RequestBody NotificationRequestDto request) {

        logger.info("Получен запрос на отправку email на: {}", request.getEmail());

        emailService.sendCustomEmail(request.getEmail(), request.getSubject(), request.getMessage());

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Email отправлен на " + request.getEmail());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/test/user-created")
    public ResponseEntity<Map<String, String>> testUserCreated(@RequestParam String email) {
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Неверный формат email");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        UserEventDto event = new UserEventDto(UserEventDto.EventType.CREATED, email);
        emailService.sendUserEventNotification(event);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Тестовое уведомление о создании пользователя отправлено на " + email);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/test/user-deleted")
    public ResponseEntity<Map<String, String>> testUserDeleted(@RequestParam String email) {
        if (email == null || email.trim().isEmpty() || !isValidEmail(email)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Неверный формат email");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        UserEventDto event = new UserEventDto(UserEventDto.EventType.DELETED, email);
        emailService.sendUserEventNotification(event);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Тестовое уведомление об удалении пользователя отправлено на " + email);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}