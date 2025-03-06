package com.bss.bssserverapi.domain.Auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LoginUserResDto {

    private String userId;
    private String accessToken;

    @Builder
    public LoginUserResDto(String userId, String accessToken) {

        this.userId = userId;
        this.accessToken = accessToken;
    }
}
