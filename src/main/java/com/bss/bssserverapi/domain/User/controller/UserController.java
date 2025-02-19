package com.bss.bssserverapi.domain.User.controller;

import com.bss.bssserverapi.domain.User.dto.CreateUserReqDto;
import com.bss.bssserverapi.domain.User.dto.CreateUserResDto;
import com.bss.bssserverapi.domain.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public CreateUserResDto createUser(@RequestBody @Validated final CreateUserReqDto createUserReqDto){

        return userService.createUser(createUserReqDto);
    }
}
