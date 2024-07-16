package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice("ru.practicum.ewm")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.debug("404 status was received: message={}, stack trace={}", e.getMessage(), e.getStackTrace());
        return new ErrorResponse("NOT_FOUND", "The required object was not found.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.debug("400 status was received: message={}, stack trace={}", e.getMessage(), e.getStackTrace());
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.debug("409 status was received: message={}, stack trace={}", e.getMessage(), e.getStackTrace());
        return new ErrorResponse("CONFLICT", "Integrity constraint has been violated.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleActionValidationException(final ActionValidationException e) {
        log.debug("409 status was received: message={}, stack trace={}", e.getMessage(), e.getStackTrace());
        return new ErrorResponse("FORBIDDEN", "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEventInitiatorValidationException(final EventInitiatorValidationException e) {
        log.debug("409 status was received: message={}, stack trace={}", e.getMessage(), e.getStackTrace());
        return new ErrorResponse("FORBIDDEN", "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDateValidationException(final DateValidationException e) {
        log.debug("400 status was received: message={}, stack trace={}", e.getMessage(), e.getStackTrace());
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.debug("400 status was received: message={}, stack trace={}", e.getMessage(), e.getStackTrace());
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestValueException(final MissingRequestValueException e) {
        log.debug("400 status was received: message={}, stack trace={}", e.getMessage(), e.getStackTrace());
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.debug("500 status was received: message={}, stack trace={}", e.getMessage(), e.getStackTrace());
        return new ErrorResponse("INTERNAL_SERVER_ERROR", "Something went wrong.", e.getMessage());
    }
}
