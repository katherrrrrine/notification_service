package org.example.validation;

import org.example.exception.InvalidEmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final Logger logger = LoggerFactory.getLogger(EmailValidator.class);
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static void validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Email пустой или null");
            throw new InvalidEmailException("Email не может быть пустым");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            logger.error("Неверный формат email: {}", email);
            throw new InvalidEmailException("Неверный формат email: " + email);
        }
    }
}