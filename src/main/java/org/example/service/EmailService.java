package org.example.service;

import org.example.dto.UserEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:test@localhost}")
    private String fromEmail;

    @Value("${app.site-name:UserService}")
    private String siteName;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendUserEventNotification(UserEventDto event) {
        if (event.getEmail() == null || event.getEmail().trim().isEmpty()) {
            logger.error("Не удалось отправить email: адрес пустой или null");
            throw new RuntimeException("Email адрес не может быть пустым");
        }

        String subject = getSubject(event.getEventType());
        String message = getMessage(event.getEventType(), event.getEmail());

        sendEmail(event.getEmail(), subject, message);
    }

    public void sendCustomEmail(String to, String subject, String message) {
        if (to == null || to.trim().isEmpty()) {
            logger.error("Не удалось отправить email: адрес пустой");
            throw new RuntimeException("Email адрес не может быть пустым");
        }
        sendEmail(to, subject, message);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            logger.info("Email успешно отправлен на {} с темой: {}", to, subject);
        } catch (Exception e) {
            logger.error("Ошибка при отправке email на {}: {}", to, e.getMessage());
            throw new RuntimeException("Не удалось отправить email: " + e.getMessage(), e);
        }
    }

    private String getSubject(UserEventDto.EventType eventType) {
        switch (eventType) {
            case CREATED:
                return "Добро пожаловать! Аккаунт успешно создан";
            case DELETED:
                return "Ваш аккаунт был удален";
            default:
                return "Уведомление от " + siteName;
        }
    }

    private String getMessage(UserEventDto.EventType eventType, String email) {
        switch (eventType) {
            case CREATED:
                return String.format("Здравствуйте!\n\n" + "Ваш аккаунт на сайте %s был успешно создан.\n\n", siteName);
            case DELETED:
                return String.format("Здравствуйте!\n\n" + "Ваш аккаунт на сайте %s был удалён.\n\n", siteName);
            default:
                return "Уведомление от " + siteName;
        }
    }
}