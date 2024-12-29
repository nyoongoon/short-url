package com.shorturl.shorturl.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.net.MalformedURLException;

import static com.shorturl.shorturl.exception.ExceptionMessageConstants.INVALID_URL_FORM;
import static com.shorturl.shorturl.exception.ExceptionMessageConstants.SERVER_ERROR;
import static org.springframework.http.HttpStatus.*;


/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 12:22 오후
 */
@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MalformedURLException.class)
    public ErrorResponse malformedURLException(MalformedURLException e) {
        log.error("MalformedURLException : {} ", e.getMessage());
        return ErrorResponse.error(BAD_REQUEST, INVALID_URL_FORM);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse constraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException : {} ", e.getMessage());
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().stream().findFirst().orElseThrow();
        String message = constraintViolation.getMessageTemplate();
        return ErrorResponse.error(BAD_REQUEST, message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException : {} ", e.getMessage());
        return ErrorResponse.error(BAD_REQUEST, e.getFieldErrors().get(0).getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFormatException.class)
    public ErrorResponse invalidFormatException(Exception e) {
        log.error("InvalidFormatException : {} ", e.getMessage());
        return ErrorResponse.error(BAD_REQUEST, INVALID_URL_FORM);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse httpMessageNotReadableException(Exception e) {
        log.error("HttpMessageNotReadableException : {} ", e.getMessage());
        return ErrorResponse.error(BAD_REQUEST, INVALID_URL_FORM);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse illegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException : {} ", e.getMessage());
        return ErrorResponse.error(BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse illegalStateException(IllegalStateException e) {
        log.error("IllegalStateException : {} ", e.getMessage());
        return ErrorResponse.error(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ResponseStatus(SERVICE_UNAVAILABLE)
    @ExceptionHandler(DuplicatedKeyException.class)
    public ErrorResponse duplicatedKeyException(DuplicatedKeyException e) {
        log.error("duplicatedKeyException : {} ", e.getMessage());
        return ErrorResponse.error(SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ErrorResponse invalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
        log.error("InvalidDataAccessApiUsageException : {} ", e.getMessage());
        return ErrorResponse.error(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse serverException(Exception e) {
        log.error("Exception : {} ", e.getMessage());
        return ErrorResponse.error(INTERNAL_SERVER_ERROR, SERVER_ERROR);
    }
}
