package com.bss.bssserverapi.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LoginUserResDto {

    private String userName;
    private String accessToken;

    @Builder
    public LoginUserResDto(final String userName, final String accessToken) {

        this.userName = userName;
        this.accessToken = accessToken;
    }
}
