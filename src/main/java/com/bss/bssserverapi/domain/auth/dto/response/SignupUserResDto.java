package com.bss.bssserverapi.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SignupUserResDto {

    private String userName;

    @Builder
    public SignupUserResDto(final String userName) {

        this.userName = userName;
    }
}
