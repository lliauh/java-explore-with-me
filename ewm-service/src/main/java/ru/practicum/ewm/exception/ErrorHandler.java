package ru.practicum.ewm.exception;

import org.hibernate.JDBCException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestControllerAdvice("ru.practicum.ewm")
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse("NOT_FOUND", "The required object was not found.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleJDBCException(final JDBCException e) {
        return new ErrorResponse("CONFLICT", "Integrity constraint has been violated.",
                e.getMessage() + "; " + e.getSQLException().getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String message = "";
        for (ConstraintViolation violation : constraintViolations) {
            message = message + "Field: " + violation.getPropertyPath() + ". Error: " + violation.getMessage() +
                    ". Value: " + violation.getInvalidValue();
        }

        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleActionValidationException(final ActionValidationException e) {
        return new ErrorResponse("FORBIDDEN", "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEventInitiatorValidationException(final EventInitiatorValidationException e) {
        return new ErrorResponse("FORBIDDEN", "For the requested operation the conditions are not met.",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDateValidationException(final DateValidationException e) {
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.",
                e.getMessage());
    }
}
