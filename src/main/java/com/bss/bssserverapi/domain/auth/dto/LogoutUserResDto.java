package com.bss.bssserverapi.domain.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LogoutUserResDto {

    private ResponseCookie cookie;

    @Builder
    public LogoutUserResDto(ResponseCookie cookie) {

        this.cookie = cookie;
    }
}
