package com.bss.bssserverapi.domain.User.controller;

import com.bss.bssserverapi.domain.User.dto.SignupUserReqDto;
import com.bss.bssserverapi.domain.User.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
