package com.bss.bssserverapi.domain.Auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LoginUserResWithCookieDto {

    private ResponseCookie cookie;
    private LoginUserResDto loginUserResDto;

    @Builder
    public LoginUserResWithCookieDto(final ResponseCookie cookie, final LoginUserResDto loginUserResDto) {

        this.cookie = cookie;
        this.loginUserResDto = loginUserResDto;
    }
}