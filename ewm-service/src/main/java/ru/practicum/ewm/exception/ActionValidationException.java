package ru.practicum.ewm.exception;

public class ActionValidationException extends RuntimeException {
    public ActionValidationException(final String message) {
        super(message);
    }
}
