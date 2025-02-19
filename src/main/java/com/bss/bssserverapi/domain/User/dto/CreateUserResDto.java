package com.bss.bssserverapi.domain.User.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CreateUserResDto {

    private String userId;
    private String accessToken;
    private String refreshToken;

    @Builder
    public CreateUserResDto(final String userId, final String accessToken, final String refreshToken) {

        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
