package com.bss.bssserverapi.domain.User.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CreateUserResDto {

    private String userId;

    @Builder
    public CreateUserResDto(final String userId) {

        this.userId = userId;
    }
}
