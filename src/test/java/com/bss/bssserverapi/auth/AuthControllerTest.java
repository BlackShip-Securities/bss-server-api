package com.bss.bssserverapi.auth;

import com.bss.bssserverapi.domain.auth.controller.AuthController;
import com.bss.bssserverapi.domain.auth.dto.request.LoginUserReqDto;
import com.bss.bssserverapi.domain.auth.dto.request.SignupUserReqDto;
import com.bss.bssserverapi.domain.auth.dto.response.*;
import com.bss.bssserverapi.domain.auth.filter.OAuth2FailureHandler;
import com.bss.bssserverapi.domain.auth.filter.OAuth2SuccessHandler;
import com.bss.bssserverapi.domain.auth.service.AuthService;
import com.bss.bssserverapi.domain.auth.service.OAuth2Service;
import com.bss.bssserverapi.domain.auth.utils.CookieProvider;
import com.bss.bssserverapi.domain.auth.utils.JwtProvider;
import com.bss.bssserverapi.global.config.SecurityConfig;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, JwtProvider.class, OAuth2SuccessHandler.class, OAuth2FailureHandler.class})
public class AuthControllerTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private OAuth2Service oAuth2Service;

    @MockBean
    @Qualifier("corsConfigurationSource")
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("회원 가입 성공")
    void createUser_Success() throws Exception {

        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .userName("bss_test")
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();

        SignupUserResDto res = SignupUserResDto.builder()
                .userName("bss_test")
                .build();

        given(authService.signupUser(any(SignupUserReqDto.class), any(String.class))).willReturn(res);

        // when & then
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("bss_test"));
    }

    @Test
    @DisplayName("회원 가입 실패 - 필수 필드 누락")
    void createUser_Fail_MissingFields() throws Exception {

        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                // TODO: GlobalException 정책 변경하고 matcher 수정
                .andExpect(content().string(not(isEmptyOrNullString())));
    }

    @Test
    @DisplayName("회원 가입 실패 - 옳바르지 못한 필드 값")
    void createUser_Fail_InvalidFieldValues() throws Exception {

        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .userName("bss_test")
                .password("invalidPW")
                .passwordConfirmation("invalidPW")
                .build();

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                // TODO: GlobalException 정책 변경하고 matcher 수정
                .andExpect(content().string(not(isEmptyOrNullString())));
    }

    @Test
    @DisplayName("인증(로그인) 성공")
    void login_Success() throws Exception {

        // given
        LoginUserReqDto req = LoginUserReqDto.builder()
                .userName("bss_test")
                .password("Qq12341234@")
                .build();

        LoginUserResWithCookieDto res = LoginUserResWithCookieDto.builder()
                .cookie(CookieProvider.createResponseCookie("rt_test", 0L))
                .loginUserResDto(LoginUserResDto.builder()
                        .accessToken("at_test")
                        .userName("bss_test")
                        .build())
                .build();

        given(authService.login(any(LoginUserReqDto.class))).willReturn(res);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("bss_test"))
                .andExpect(jsonPath("$.accessToken").value("at_test"))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().value("refresh_token", "rt_test"));
    }

    @Test
    @DisplayName("인가 실패 - 액세스 토큰 없음")
    void authorization_Fail_NoAccessToken() throws Exception {

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/auth/token-test")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(""))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHENTICATED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHENTICATED.getMessage()));
    }


    @Test
    @DisplayName("액세스 토큰 검증 실패 - 만료 시간")
    void tokenValid_Fail_Expired() throws Exception {

        // given
        String accessToken = jwtProvider.createExpiredAccessToken("bss_test", "ROLE_USER");

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/auth/token-test")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(""))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.EXPIRED_TOKEN.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.EXPIRED_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("액세스 토큰 검증 실패 - 위변조")
    void tokenValid_Fail_Tampered() throws Exception {

        // given
        String accessToken = jwtProvider.createAccessToken("bss_test", "ROLE_USER");
        accessToken = jwtProvider.createTamperedAccessToken(accessToken, "bss_admin");

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/auth/token-test")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(""))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_TOKEN.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_TOKEN.getMessage()));
    }

    @Test
    @DisplayName("액세스 토큰 재발급(리프레시) 성공")
    void refreshToken_Success() throws Exception {

        // given
        String refreshToken = "rt_test";
        RefreshTokenResWithCookieDto res = RefreshTokenResWithCookieDto.builder()
                .cookie(CookieProvider.createResponseCookie(refreshToken, 0L))
                .refreshTokenResDto(RefreshTokenResDto.builder()
                        .accessToken("at_test")
                        .build())
                .build();

        given(authService.refresh(anyString())).willReturn(res);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
                        .cookie(new Cookie("refresh_token", refreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(jsonPath("$.accessToken").value("at_test"));
    }
}
