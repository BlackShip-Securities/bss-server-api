package com.bss.bssserverapi.domain.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RefreshTokenResDto {

    private String accessToken;

    @Builder
    public RefreshTokenResDto(String accessToken) {

        this.accessToken = accessToken;
    }
}
