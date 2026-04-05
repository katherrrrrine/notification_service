package org.example.controller;

import org.example.dto.NotificationRequestDto;
import org.example.dto.UserEventDto;
import org.example.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final EmailService emailService;

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
        logger.info("Тестовая отправка уведомления о создании пользователя: {}", email);

        UserEventDto event = new UserEventDto(UserEventDto.EventType.CREATED, email);
        emailService.sendUserEventNotification(event);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Тестовое уведомление о создании пользователя отправлено на " + email);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/test/user-deleted")
    public ResponseEntity<Map<String, String>> testUserDeleted(@RequestParam String email) {
        logger.info("Тестовая отправка уведомления об удалении пользователя: {}", email);

        UserEventDto event = new UserEventDto(UserEventDto.EventType.DELETED, email);
        emailService.sendUserEventNotification(event);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Тестовое уведомление об удалении пользователя отправлено на " + email);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}