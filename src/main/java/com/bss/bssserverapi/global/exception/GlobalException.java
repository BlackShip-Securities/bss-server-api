package com.bss.bssserverapi.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public GlobalException(final HttpStatus httpStatus, final ErrorCode errorCode){
        super(errorCode.getMessage());
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
