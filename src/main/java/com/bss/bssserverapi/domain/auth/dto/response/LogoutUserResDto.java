package com.bss.bssserverapi.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
public class LogoutUserResDto {

    private ResponseCookie cookie;

    @Builder
    public LogoutUserResDto(ResponseCookie cookie) {

        this.cookie = cookie;
    }
}
