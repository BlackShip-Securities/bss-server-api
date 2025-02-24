package com.bss.bssserverapi.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO: IllegalArgumentException, MethodArgumentNotValidException, etc..

    /**
     * 커스텀 예외 처리
     */
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<?> handleGlobalException(final GlobalException e){

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(e.getErrorCode());
    }

    /**
     * 그 외 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(final Exception e){

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorCode.UNKNOWN_SERVER_ERROR);
    }
}
