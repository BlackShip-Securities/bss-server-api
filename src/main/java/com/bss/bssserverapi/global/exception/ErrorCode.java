package com.bss.bssserverapi.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // auth

    // business

    // common
    INTERNAL_SERVER_ERROR("C001", "Internal Server Error"),
    ;

    private final String code;
    private final String message;

    ErrorCode(final String code, final String message){
        this.code = code;
        this.message = message;
    }
}
