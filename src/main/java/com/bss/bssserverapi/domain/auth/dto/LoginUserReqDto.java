package com.bss.bssserverapi.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class LoginUserReqDto {

    @NotNull
    @Schema(example = "bss_test")
    private String userId;

    @NotNull
    @Schema(example = "Qq12341234@")
    private String password;

    @Builder
    public LoginUserReqDto(String userId, String password) {

        this.userId = userId;
        this.password = password;
    }
}
