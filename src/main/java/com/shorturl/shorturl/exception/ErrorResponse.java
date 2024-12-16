package com.shorturl.shorturl.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author seokbin-yoon
 * @version 0.1.0
 * @since 2024/12/14 12:22 오후
 */
@Getter
public class ErrorResponse {
    private int code;
    private String status;
    private String message;

    private ErrorResponse(HttpStatus status, String message) {
        this.code = status.value();
        this.status = status.name();
        this.message = message;
    }

    public static ErrorResponse error(HttpStatus status, String message) {
        return new ErrorResponse(status, message);
    }
}
