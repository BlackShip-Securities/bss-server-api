package com.bss.bssserverapi.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GetUserResDto {

    private String userName;

    @Builder
    public GetUserResDto(String userName) {

        this.userName = userName;
    }
}
