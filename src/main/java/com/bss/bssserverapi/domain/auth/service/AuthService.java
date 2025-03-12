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

        User user = userRepository.findByUserName(loginUserReqDto.getUserName())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        if(!bCryptPasswordEncoder.matches(loginUserReqDto.getPassword(), user.getPassword())){

            throw new GlobalException(HttpStatus.UNAUTHORIZED, ErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserName());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserName());

        authRepository.deleteByUserName(user.getUserName());
        authRepository.save(user.getUserName(), refreshToken, jwtProvider.getExpiredDate(refreshToken));

        return LoginUserResWithCookieDto.builder()
                .cookie(CookieProvider.createResponseCookie(refreshToken, jwtProvider.getRefreshTokenExpiredTime() / 1000))
                .loginUserResDto(LoginUserResDto.builder()
                        .userName(user.getUserName())
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

        String userName = jwtProvider.getUserName(refreshToken);
        String accessToken = jwtProvider.createAccessToken(userName);
        String newRefreshToken = jwtProvider.createRefreshToken(userName);

        authRepository.deleteByUserName(userName);
        authRepository.save(userName, newRefreshToken, jwtProvider.getExpiredDate(newRefreshToken));

        return RefreshTokenResWithCookieDto.builder()
                .cookie(CookieProvider.createResponseCookie(newRefreshToken, jwtProvider.getRefreshTokenExpiredTime() / 1000))
                .refreshTokenResDto(RefreshTokenResDto.builder()
                        .accessToken(accessToken)
                        .build())
                .build();
    }

    @Transactional
    public LogoutUserResDto logout(final String userName){

        authRepository.deleteByUserName(userName);

        return LogoutUserResDto.builder()
                .cookie(CookieProvider.deleteResponseCookie())
                .build();
    }
}
