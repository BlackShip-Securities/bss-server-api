package com.bss.bssserverapi.domain.User.controller;

import com.bss.bssserverapi.domain.User.dto.GetUserResDto;
import com.bss.bssserverapi.domain.User.dto.SignupUserReqDto;
import com.bss.bssserverapi.domain.User.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody @Valid final SignupUserReqDto signupUserReqDto){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.signupUser(signupUserReqDto));
    }

    @GetMapping("/me")
    public ResponseEntity<GetUserResDto> getUser(@AuthenticationPrincipal final String userId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUser(userId));
    }
}
