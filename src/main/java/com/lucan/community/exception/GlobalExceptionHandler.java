package com.lucan.community.exception;

import com.lucan.community.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException e) {

        if ("email_already_exists".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("email_already_exists", null));
        }

        if ("nickname_already_exists".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("nickname_already_exists", null));
        }

        if ("password_not_match".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("invalid_request", null));
        }

        if ("login_failed".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("login_failed", null));
        }

        if ("post_not_found".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("post_not_found", null));
        }

        if ("comment_not_found".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("comment_not_found", null));
        }

        if ("login_required".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("login_required", null));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse("invalid_request", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse("invalid_request", null));
    }
}