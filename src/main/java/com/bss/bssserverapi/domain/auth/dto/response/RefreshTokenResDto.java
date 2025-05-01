package com.bss.bssserverapi.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshTokenResDto {

    private String accessToken;

    @Builder
    public RefreshTokenResDto(String accessToken) {

        this.accessToken = accessToken;
    }
}
