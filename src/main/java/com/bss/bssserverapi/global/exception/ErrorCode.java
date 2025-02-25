package com.bss.bssserverapi.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // auth
    USER_ALREADY_EXISTS("A001", "User already exists"),
    // business
    // common
    UNKNOWN_SERVER_ERROR("C001", "Unknown Server Error"),
    ;

    private final String code;
    private final String message;

    ErrorCode(final String code, final String message){
        this.code = code;
        this.message = message;
    }
}
