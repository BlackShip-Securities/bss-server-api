package com.bss.bssserverapi.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
public class RefreshTokenResWithCookieDto {

    private ResponseCookie cookie;
    private RefreshTokenResDto refreshTokenResDto;

    @Builder
    public RefreshTokenResWithCookieDto(ResponseCookie cookie, RefreshTokenResDto refreshTokenResDto) {

        this.cookie = cookie;
        this.refreshTokenResDto = refreshTokenResDto;
    }
}
