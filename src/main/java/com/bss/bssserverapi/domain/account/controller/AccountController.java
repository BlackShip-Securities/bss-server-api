package com.bss.bssserverapi.domain.account.controller;

import com.bss.bssserverapi.domain.account.dto.GetAccountResDto;
import com.bss.bssserverapi.domain.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping()
    public ResponseEntity<GetAccountResDto> getAccount(@AuthenticationPrincipal final String userName) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountService.getAccount(userName));
    }
}
