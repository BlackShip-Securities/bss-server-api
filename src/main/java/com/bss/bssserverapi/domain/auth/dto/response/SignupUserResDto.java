package com.bss.bssserverapi.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignupUserResDto {

    private String userName;

    @Builder
    public SignupUserResDto(final String userName) {

        this.userName = userName;
    }
}
