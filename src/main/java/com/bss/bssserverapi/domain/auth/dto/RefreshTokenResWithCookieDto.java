package com.bss.bssserverapi.domain.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RefreshTokenResWithCookieDto {

    private ResponseCookie cookie;
    private RefreshTokenResDto refreshTokenResDto;

    @Builder
    public RefreshTokenResWithCookieDto(ResponseCookie cookie, RefreshTokenResDto refreshTokenResDto) {

        this.cookie = cookie;
        this.refreshTokenResDto = refreshTokenResDto;
    }
}
