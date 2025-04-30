package com.bss.bssserverapi.auth;

import com.bss.bssserverapi.domain.auth.dto.request.LoginUserReqDto;
import com.bss.bssserverapi.domain.auth.dto.request.SignupUserReqDto;
import com.bss.bssserverapi.domain.auth.dto.response.LoginUserResWithCookieDto;
import com.bss.bssserverapi.domain.auth.dto.response.LogoutUserResDto;
import com.bss.bssserverapi.domain.auth.dto.response.RefreshTokenResWithCookieDto;
import com.bss.bssserverapi.domain.auth.dto.response.SignupUserResDto;
import com.bss.bssserverapi.domain.auth.repository.AuthRepository;
import com.bss.bssserverapi.domain.auth.service.AuthService;
import com.bss.bssserverapi.domain.auth.utils.JwtProvider;
import com.bss.bssserverapi.domain.user.RoleType;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    @DisplayName("회원 가입 성공")
    public void createUser_Success() {

        // given
        String guestUserName = "googleiii";
        String encodedPassword = bCryptPasswordEncoder.encode("Qq12341234@");

        SignupUserReqDto req = SignupUserReqDto.builder()
                .userName("bss_admin")
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();

        User guestUser = User.builder()
                .userName(guestUserName)
                .password(encodedPassword)
                .roleType(RoleType.ROLE_GUEST)
                .build();

        User updatedUser = User.builder()
                .userName(req.getUserName())
                .password(encodedPassword)
                .roleType(RoleType.ROLE_USER)
                .build();

        // stub
        Mockito.when(userJpaRepository.findByUserName(guestUserName))
                .thenReturn(Optional.of(guestUser));
        Mockito.when(userJpaRepository.existsByUserName(req.getUserName()))
                .thenReturn(false);
        Mockito.when(userJpaRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        // when
        SignupUserResDto res = authService.signupUser(req, guestUserName);

        // then
        assertThat(res.getUserName()).isEqualTo(req.getUserName());
    }

    @Test
    @DisplayName("회원 가입 실패 - 비밀번호, 비밀번호 확인 일치하지 않음")
    public void createUser_Fail_PasswordMismatch() {

        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .userName("bss_admin")
                .password("Qq12341234@!!!!!!!!!!!!")
                .passwordConfirmation("Qq12341234@")
                .build();
        String guestUserName = "googleiii";

        User guestUser = User.builder()
                .userName(guestUserName)
                .password("guest_pw")
                .roleType(RoleType.ROLE_GUEST)
                .build();

        Mockito.when(userJpaRepository.findByUserName(guestUserName)).thenReturn(Optional.of(guestUser));
        Mockito.when(userJpaRepository.existsByUserName(req.getUserName())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.signupUser(req, guestUserName))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ErrorCode.PASSWORD_AND_CONFIRMATION_MISMATCH.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 존재하는 유저네임")
    public void createUser_Fail_UserAlreadyExists() {
        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .userName("bss_admin")
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();
        String guestUserName = "googleiii";

        User guestUser = User.builder()
                .userName(guestUserName)
                .password("guest_pw")
                .roleType(RoleType.ROLE_GUEST)
                .build();

        Mockito.when(userJpaRepository.findByUserName(guestUserName)).thenReturn(Optional.of(guestUser));
        Mockito.when(userJpaRepository.existsByUserName(req.getUserName())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signupUser(req, guestUserName))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ErrorCode.USER_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("회원 가입 실패 - 게스트 유저 존재하지 않음")
    public void createUser_Fail_GuestNotFound() {
        // given
        SignupUserReqDto req = SignupUserReqDto.builder()
                .userName("bss_admin")
                .password("Qq12341234@")
                .passwordConfirmation("Qq12341234@")
                .build();
        String guestUserName = "unknown_guest";

        Mockito.when(userJpaRepository.findByUserName(guestUserName)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.signupUser(req, guestUserName))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

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
                .roleType(RoleType.ROLE_USER)
                .build();

        LoginUserReqDto req = new LoginUserReqDto(userName, password);

        when(userJpaRepository.findByUserName(userName)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtProvider.createAccessToken(userName, RoleType.ROLE_USER.name())).thenReturn(accessToken);
        when(jwtProvider.createRefreshToken(userName, RoleType.ROLE_USER.name())).thenReturn(refreshToken);
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
        when(jwtProvider.getRole(refreshToken)).thenReturn(RoleType.ROLE_USER.name());
        when(jwtProvider.createAccessToken(userName, RoleType.ROLE_USER.name())).thenReturn(newAccessToken);
        when(jwtProvider.createRefreshToken(userName, RoleType.ROLE_USER.name())).thenReturn(newRefreshToken);
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