package com.bss.bssserverapi.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetUserResDto {

    private String userName;

    @Builder
    public GetUserResDto(String userName) {

        this.userName = userName;
    }
}
