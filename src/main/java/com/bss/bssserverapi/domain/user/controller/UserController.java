package com.bss.bssserverapi.domain.user.controller;

import com.bss.bssserverapi.domain.user.dto.GetUserResDto;
import com.bss.bssserverapi.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<GetUserResDto> getUser(@AuthenticationPrincipal final String userName) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUser(userName));
    }
}
