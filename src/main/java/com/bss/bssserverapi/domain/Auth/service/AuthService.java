package com.bss.bssserverapi.domain.Auth.service;

import com.bss.bssserverapi.domain.Auth.dto.LoginUserReqDto;
import com.bss.bssserverapi.domain.Auth.dto.LoginUserResDto;
import com.bss.bssserverapi.domain.Auth.dto.LoginUserResWithCookieDto;
import com.bss.bssserverapi.domain.Auth.repository.AuthRepository;
import com.bss.bssserverapi.domain.Auth.utils.JwtProvider;
import com.bss.bssserverapi.domain.User.User;
import com.bss.bssserverapi.domain.User.repository.UserRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;

    public LoginUserResWithCookieDto login(final LoginUserReqDto loginUserReqDto){

        User user = userRepository.findByUserId(loginUserReqDto.getUserId())
                .orElseThrow(() -> new GlobalException(HttpStatus.UNAUTHORIZED, ErrorCode.USER_NOT_FOUND));

        if(!bCryptPasswordEncoder.matches(loginUserReqDto.getPassword(), user.getPassword())){

            throw new GlobalException(HttpStatus.UNAUTHORIZED, ErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        authRepository.delete(user.getUserId());
        authRepository.save(user.getUserId(), refreshToken, jwtProvider.getExpiredDate(refreshToken));

        return LoginUserResWithCookieDto.builder()
                .cookie(this.createResponseCookie(refreshToken))
                .loginUserResDto(LoginUserResDto.builder()
                        .userId(user.getUserId())
                        .accessToken(accessToken)
                        .build())
                .build();
    }

    private ResponseCookie createResponseCookie(String refreshToken){

        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true) // XSS 방지
                .secure(true) // HTTPS 에서만 전송
//                .sameSite("Strict") // CSRF 방지
                .path("/")
                .maxAge(jwtProvider.getRefreshTokenExpiredTime() / 1000)
                .build();
    }
}
