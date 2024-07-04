package ru.practicum.stats.validation;

public class ValidationException extends RuntimeException {
    public ValidationException(final String message) {
        super(message);
    }
}
