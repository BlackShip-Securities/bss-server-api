package com.bss.bssserverapi.domain.Auth.controller;

import com.bss.bssserverapi.domain.Auth.dto.LoginUserReqDto;
import com.bss.bssserverapi.domain.Auth.dto.LoginUserResDto;
import com.bss.bssserverapi.domain.Auth.dto.LoginUserResWithCookieDto;
import com.bss.bssserverapi.domain.Auth.dto.RefreshTokenResWithCookieDto;
import com.bss.bssserverapi.domain.Auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                .header("Set-Cookie", loginUserResWithCookieDto.getCookie().toString())
                .body(loginUserResWithCookieDto.getLoginUserResDto());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken) {

        RefreshTokenResWithCookieDto refreshTokenResWithCookieDto = authService.refresh(refreshToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Set-Cookie", refreshTokenResWithCookieDto.getCookie().toString())
                .body(refreshTokenResWithCookieDto.getRefreshTokenResDto());
    }
}
