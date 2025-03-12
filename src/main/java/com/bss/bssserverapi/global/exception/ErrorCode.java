package com.bss.bssserverapi.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // auth
    USER_ALREADY_EXISTS("A001", "User already exists"),
    PASSWORD_AND_CONFIRMATION_MISMATCH("A002", "Password and confirmation do not match"),
    USER_NOT_FOUND("A003", "User not found"),
    PASSWORD_MISMATCH("A004", "Password do not match"),
    UNAUTHENTICATED("A005", "Unauthenticated"),
    UNAUTHORIZED("A006", "Unauthorized"),
    EXPIRED_TOKEN("A007", "Token is expired"),
    INVALID_TOKEN("A008", "Token is invalid"),

    // business
    STOCK_NOT_FOUND("B003", "Stock not found"),

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
