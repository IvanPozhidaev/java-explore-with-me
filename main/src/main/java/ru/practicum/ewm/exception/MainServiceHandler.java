package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MainServiceHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MainErrorResponse handleNotFound(final MainNotFoundException e) {
        log.error("The required object was not found. {}", e.getMessage());
        return new MainErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MainErrorResponse handleParameter(final MainParameterException e) {
        log.error("Incorrectly made request. {}", e.getMessage());
        return new MainErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public MainErrorResponse handleParamConflict(final MainParamConflictException e) {
        log.error("Incorrectly made request. {}", e.getMessage());
        return new MainErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public MainErrorResponse handleNotUnique(final DataIntegrityViolationException e) {
        log.error("Integrity constraint has been violated. {}", e.getMessage());
        return new MainErrorResponse(e.getMessage());
    }
}