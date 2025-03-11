package com.bss.bssserverapi.domain.auth.service;

import com.bss.bssserverapi.domain.auth.dto.*;
import com.bss.bssserverapi.domain.auth.repository.AuthRepository;
import com.bss.bssserverapi.domain.auth.utils.CookieProvider;
import com.bss.bssserverapi.domain.auth.utils.JwtProvider;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public LoginUserResWithCookieDto login(final LoginUserReqDto loginUserReqDto){

        User user = userRepository.findByUserId(loginUserReqDto.getUserId())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        if(!bCryptPasswordEncoder.matches(loginUserReqDto.getPassword(), user.getPassword())){

            throw new GlobalException(HttpStatus.UNAUTHORIZED, ErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        authRepository.delete(user.getUserId());
        authRepository.save(user.getUserId(), refreshToken, jwtProvider.getExpiredDate(refreshToken));

        return LoginUserResWithCookieDto.builder()
                .cookie(CookieProvider.createResponseCookie(refreshToken, jwtProvider.getRefreshTokenExpiredTime() / 1000))
                .loginUserResDto(LoginUserResDto.builder()
                        .userId(user.getUserId())
                        .accessToken(accessToken)
                        .build())
                .build();
    }

    @Transactional
    public RefreshTokenResWithCookieDto refresh(String refreshToken){

        if (!StringUtils.hasText(refreshToken)) {

            throw new GlobalException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHENTICATED);
        }

        jwtProvider.validateToken(refreshToken);

        String userId = jwtProvider.getUserId(refreshToken);
        String accessToken = jwtProvider.createAccessToken(userId);
        String newRefreshToken = jwtProvider.createRefreshToken(userId);

        authRepository.delete(userId);
        authRepository.save(userId, refreshToken, jwtProvider.getExpiredDate(newRefreshToken));

        return RefreshTokenResWithCookieDto.builder()
                .cookie(CookieProvider.createResponseCookie(refreshToken, jwtProvider.getRefreshTokenExpiredTime() / 1000))
                .refreshTokenResDto(RefreshTokenResDto.builder()
                        .accessToken(accessToken)
                        .build())
                .build();
    }

    @Transactional
    public LogoutUserResDto logout(final String userId){

        authRepository.delete(userId);

        return LogoutUserResDto.builder()
                .cookie(CookieProvider.deleteResponseCookie())
                .build();
    }
}
