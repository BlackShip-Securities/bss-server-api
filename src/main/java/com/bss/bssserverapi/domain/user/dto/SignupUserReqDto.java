package com.bss.bssserverapi.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupUserReqDto {

    @NotNull
    @Pattern(regexp = "^[a-z0-9._]{4,30}$",
            message = "아이디는 4자리에서 30자리 사이의 영문 소문자, 숫자, 밑줄(_), 마침표(.)만 가능합니다.")
    @Schema(example = "bss_test")
    private String userName;

    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "8자리에서 20자리, 영 대/소문자, 숫자 한 번씩 포함, !, @, #, $, %, ^, &, +, = 등의 문자를 한 번씩 포함, 띄어쓰기/탭 금지")
    @Schema(example = "Qq12341234@")
    private String password;

    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "8자리에서 20자리, 영 대/소문자, 숫자 한 번씩 포함, !, @, #, $, %, ^, &, +, = 등의 문자를 한 번씩 포함, 띄어쓰기/탭 금지")
    @Schema(example = "Qq12341234@")
    private String passwordConfirmation;

    @Builder
    public SignupUserReqDto(final String userName, final String password, final String passwordConfirmation) {
        this.userName = userName;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
}
