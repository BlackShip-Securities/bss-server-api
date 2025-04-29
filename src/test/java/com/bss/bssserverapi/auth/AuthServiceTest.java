package com.bss.bssserverapi.auth;

import com.bss.bssserverapi.domain.auth.dto.request.LoginUserReqDto;
import com.bss.bssserverapi.domain.auth.dto.response.LoginUserResWithCookieDto;
import com.bss.bssserverapi.domain.auth.dto.response.LogoutUserResDto;
import com.bss.bssserverapi.domain.auth.dto.response.RefreshTokenResWithCookieDto;
import com.bss.bssserverapi.domain.auth.repository.AuthRepository;
import com.bss.bssserverapi.domain.auth.service.AuthService;
import com.bss.bssserverapi.domain.auth.utils.JwtProvider;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {

        // given
        String userName = "bss_test";
        String password = "Qq12341234@";
        String hashedPassword = "Qq12341234@_hashed";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        User user = User.builder()
                .userName(userName)
                .password(hashedPassword)
                .build();

        LoginUserReqDto req = new LoginUserReqDto(userName, password);

        when(userJpaRepository.findByUserName(userName)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtProvider.createAccessToken(userName, "ROLE_USER")).thenReturn(accessToken);
        when(jwtProvider.createRefreshToken(userName, "ROLE_USER")).thenReturn(refreshToken);
        doNothing().when(authRepository).deleteByUserName(userName);
        doNothing().when(authRepository).save(eq(userName), eq(refreshToken), any());

        // when
        LoginUserResWithCookieDto res = authService.login(req);

        // then
        assertThat(res.getLoginUserResDto().getUserName()).isEqualTo(userName);
        assertThat(res.getLoginUserResDto().getAccessToken()).isEqualTo(accessToken);
        assertThat(res.getCookie().getValue()).isEqualTo(refreshToken);

        verify(authRepository).deleteByUserName(userName);
        verify(authRepository).save(eq(userName), eq(refreshToken), any());
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void login_Fail_UserNotFound() {

        // given
        String userName = "bss_test";
        LoginUserReqDto loginUserReqDto = new LoginUserReqDto(userName, "password");

        when(userJpaRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginUserReqDto))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(authRepository, never()).deleteByUserName(userName);
        verify(authRepository, never()).save(any(), any(), any());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_Fail_PasswordMismatch() {

        // given
        String userName = "bss_test";
        String password = "Qq12341234@_wrong";
        String hashedPassword = "Qq12341234@_wrong_hashed";

        User user = User.builder()
                .userName(userName)
                .password(hashedPassword)
                .build();

        LoginUserReqDto loginUserReqDto = new LoginUserReqDto(userName, password);

        when(userJpaRepository.findByUserName(userName)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(password, hashedPassword)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginUserReqDto))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_MISMATCH);

        verify(authRepository, never()).deleteByUserName(userName);
        verify(authRepository, never()).save(any(), any(), any());
    }

    @Test
    @DisplayName("리프레시 토큰 성공")
    void refresh_Success() {

        // given
        String userName = "bss_test";
        String refreshToken = "refresh_token";
        String newRefreshToken = "new_refresh_token";
        String newAccessToken = "new_access_token";


        doNothing().when(jwtProvider).validateToken(refreshToken);
        when(jwtProvider.getUserName(refreshToken)).thenReturn(userName);
        when(jwtProvider.createAccessToken(userName, "ROLE_USER")).thenReturn(newAccessToken);
        when(jwtProvider.createRefreshToken(userName, "ROLE_USER")).thenReturn(newRefreshToken);
        doNothing().when(authRepository).deleteByUserName(userName);
        doNothing().when(authRepository).save(eq(userName), eq(newRefreshToken), any());

        // when
        RefreshTokenResWithCookieDto result = authService.refresh(refreshToken);

        // then
        assertThat(result.getRefreshTokenResDto().getAccessToken()).isEqualTo(newAccessToken);
        assertThat(result.getCookie().getValue()).isEqualTo(newRefreshToken);

        verify(authRepository).deleteByUserName(userName);
        verify(authRepository).save(eq(userName), eq(newRefreshToken), any());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() {

        // given
        String userName = "bss_test";

        doNothing().when(authRepository).deleteByUserName(userName);

        // when
        LogoutUserResDto result = authService.logout(userName);

        // then
        assertThat(result.getCookie()).isNotNull();

        verify(authRepository).deleteByUserName(userName);
    }
}