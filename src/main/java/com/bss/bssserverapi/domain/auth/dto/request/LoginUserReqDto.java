package com.bss.bssserverapi.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginUserReqDto {

    @NotNull
    @Schema(example = "bss_test_0")
    private String userName;

    @NotNull
    @Schema(example = "Qq12341234@")
    private String password;

    @Builder
    public LoginUserReqDto(String userName, String password) {

        this.userName = userName;
        this.password = password;
    }
}
