package com.bss.bssserverapi.domain.auth.utils;

import org.springframework.http.ResponseCookie;

public class CookieProvider {

    public static ResponseCookie createResponseCookie(final String refreshToken, final Long expiredTime){

        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true) // XSS 방지
                .secure(true) // HTTPS 에서만 전송
//                .sameSite("Strict") // CSRF 방지
                .path("/")
                .maxAge(expiredTime)
                .build();
    }

    public static ResponseCookie deleteResponseCookie(){

        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true) // XSS 방지
                .secure(true) // HTTPS 에서만 전송
//                .sameSite("Strict") // CSRF 방지
                .path("/")
                .maxAge(0)
                .build();
    }
}
