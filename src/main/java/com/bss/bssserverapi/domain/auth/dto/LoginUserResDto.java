package com.bss.bssserverapi.domain.auth.dto;

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
    public LoginUserResDto(final String userId, final String accessToken) {

        this.userId = userId;
        this.accessToken = accessToken;
    }
}
