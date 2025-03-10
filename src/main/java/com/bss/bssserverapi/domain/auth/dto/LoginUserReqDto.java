package com.bss.bssserverapi.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginUserReqDto {

    @NotNull
    @Schema(example = "bss_test")
    private String userId;

    @NotNull
    @Schema(example = "Qq12341234@")
    private String password;
}
