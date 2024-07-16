package ru.practicum.ewm.exception;

public class DateValidationException extends RuntimeException {
    public DateValidationException(final String message) {
        super(message);
    }
}
