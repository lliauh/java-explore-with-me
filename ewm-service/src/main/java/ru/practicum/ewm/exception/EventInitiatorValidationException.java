package ru.practicum.ewm.exception;

public class EventInitiatorValidationException extends RuntimeException {
    public EventInitiatorValidationException(final String message) {
        super(message);
    }
}
