package org.example.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserEventDto;
import org.example.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserEventConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topic.user-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserEvent(String message) {
        logger.info("Получено сообщение из Kafka: {}", message);

        try {
            UserEventDto event = objectMapper.readValue(message, UserEventDto.class);
            logger.info("Обработка события: {} для пользователя {}", event.getEventType(), event.getEmail());

            emailService.sendUserEventNotification(event);

            logger.info("Уведомление отправлено на email: {}", event.getEmail());
        } catch (Exception e) {
            logger.error("Ошибка при обработке сообщения: {}", e.getMessage(), e);
        }
    }
}