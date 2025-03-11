package com.bss.bssserverapi.domain.auth;

import com.bss.bssserverapi.domain.auth.dto.LoginUserReqDto;
import com.bss.bssserverapi.domain.auth.dto.LoginUserResWithCookieDto;
import com.bss.bssserverapi.domain.auth.dto.LogoutUserResDto;
import com.bss.bssserverapi.domain.auth.dto.RefreshTokenResWithCookieDto;
import com.bss.bssserverapi.domain.auth.repository.AuthRepository;
import com.bss.bssserverapi.domain.auth.service.AuthService;
import com.bss.bssserverapi.domain.auth.utils.JwtProvider;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserRepository;
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
    private UserRepository userRepository;

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
        String userId = "bss_test";
        String password = "Qq12341234@";
        String hashedPassword = "Qq12341234@_hashed";
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        User user = User.builder()
                .userId(userId)
                .password(hashedPassword)
                .build();

        LoginUserReqDto req = new LoginUserReqDto(userId, password);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtProvider.createAccessToken(userId)).thenReturn(accessToken);
        when(jwtProvider.createRefreshToken(userId)).thenReturn(refreshToken);
        doNothing().when(authRepository).deleteByUserId(userId);
        doNothing().when(authRepository).save(eq(userId), eq(refreshToken), any());

        // when
        LoginUserResWithCookieDto res = authService.login(req);

        // then
        assertThat(res.getLoginUserResDto().getUserId()).isEqualTo(userId);
        assertThat(res.getLoginUserResDto().getAccessToken()).isEqualTo(accessToken);
        assertThat(res.getCookie().getValue()).isEqualTo(refreshToken);

        verify(authRepository).deleteByUserId(userId);
        verify(authRepository).save(eq(userId), eq(refreshToken), any());
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void login_Fail_UserNotFound() {

        // given
        String userId = "bss_test";
        LoginUserReqDto loginUserReqDto = new LoginUserReqDto(userId, "password");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginUserReqDto))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(authRepository, never()).deleteByUserId(userId);
        verify(authRepository, never()).save(any(), any(), any());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_Fail_PasswordMismatch() {

        // given
        String userId = "bss_test";
        String password = "Qq12341234@_wrong";
        String hashedPassword = "Qq12341234@_wrong_hashed";

        User user = User.builder()
                .userId(userId)
                .password(hashedPassword)
                .build();

        LoginUserReqDto loginUserReqDto = new LoginUserReqDto(userId, password);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(password, hashedPassword)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginUserReqDto))
                .isInstanceOf(GlobalException.class)
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNAUTHORIZED)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_MISMATCH);

        verify(authRepository, never()).deleteByUserId(userId);
        verify(authRepository, never()).save(any(), any(), any());
    }

    @Test
    @DisplayName("리프레시 토큰 성공")
    void refresh_Success() {

        // given
        String userId = "bss_test";
        String refreshToken = "refresh_token";
        String newRefreshToken = "new_refresh_token";
        String newAccessToken = "new_access_token";


        doNothing().when(jwtProvider).validateToken(refreshToken);
        when(jwtProvider.getUserId(refreshToken)).thenReturn(userId);
        when(jwtProvider.createAccessToken(userId)).thenReturn(newAccessToken);
        when(jwtProvider.createRefreshToken(userId)).thenReturn(newRefreshToken);
        doNothing().when(authRepository).deleteByUserId(userId);
        doNothing().when(authRepository).save(eq(userId), eq(newRefreshToken), any());

        // when
        RefreshTokenResWithCookieDto result = authService.refresh(refreshToken);

        // then
        assertThat(result.getRefreshTokenResDto().getAccessToken()).isEqualTo(newAccessToken);
        assertThat(result.getCookie().getValue()).isEqualTo(newRefreshToken);

        verify(authRepository).deleteByUserId(userId);
        verify(authRepository).save(eq(userId), eq(newRefreshToken), any());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() {

        // given
        String userId = "bss_test";

        doNothing().when(authRepository).deleteByUserId(userId);

        // when
        LogoutUserResDto result = authService.logout(userId);

        // then
        assertThat(result.getCookie()).isNotNull();

        verify(authRepository).deleteByUserId(userId);
    }
}