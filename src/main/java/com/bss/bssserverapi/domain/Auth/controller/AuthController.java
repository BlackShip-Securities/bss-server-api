package com.bss.bssserverapi.domain.Auth.controller;

import com.bss.bssserverapi.domain.Auth.dto.LoginUserReqDto;
import com.bss.bssserverapi.domain.Auth.dto.LoginUserResDto;
import com.bss.bssserverapi.domain.Auth.dto.LoginUserResWithCookieDto;
import com.bss.bssserverapi.domain.Auth.dto.RefreshTokenResWithCookieDto;
import com.bss.bssserverapi.domain.Auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginUserResDto> login(@Valid @RequestBody final LoginUserReqDto loginUserReqDto){

        LoginUserResWithCookieDto loginUserResWithCookieDto = authService.login(loginUserReqDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, loginUserResWithCookieDto.getCookie().toString())
                .body(loginUserResWithCookieDto.getLoginUserResDto());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", required = false) final String refreshToken) {

        RefreshTokenResWithCookieDto refreshTokenResWithCookieDto = authService.refresh(refreshToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenResWithCookieDto.getCookie().toString())
                .body(refreshTokenResWithCookieDto.getRefreshTokenResDto());
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal final String userId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, authService.logout(userId).getCookie().toString())
                .body("");
    }
}
