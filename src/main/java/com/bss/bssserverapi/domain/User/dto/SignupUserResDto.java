package com.bss.bssserverapi.domain.User.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class SignupUserResDto {

    private String userId;

    @Builder
    public SignupUserResDto(final String userId) {

        this.userId = userId;
    }
}
