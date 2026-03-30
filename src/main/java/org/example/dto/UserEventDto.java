package org.example.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserEventDto {

    public enum EventType {
        CREATED, DELETED
    }

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String message;
    private Long userId;
    private String timestamp;

    public UserEventDto(EventType eventType, String email) {
        this.eventType = eventType;
        this.email = email;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserEventDto{" +
                "eventType=" + eventType +
                ", email='" + email + '\'' +
                ", userId=" + userId +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}