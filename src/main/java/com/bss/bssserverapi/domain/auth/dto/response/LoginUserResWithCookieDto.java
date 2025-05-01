package com.bss.bssserverapi.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
public class LoginUserResWithCookieDto {

    private ResponseCookie cookie;
    private LoginUserResDto loginUserResDto;

    @Builder
    public LoginUserResWithCookieDto(final ResponseCookie cookie, final LoginUserResDto loginUserResDto) {

        this.cookie = cookie;
        this.loginUserResDto = loginUserResDto;
    }
}