package com.bss.bssserverapi.domain.account.service;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.account.dto.GetAccountResDto;
import com.bss.bssserverapi.domain.account.repository.AccountJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountJpaRepository accountJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public GetAccountResDto getAccount(final String userName) {

        final User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        final Account account = accountJpaRepository.findAccountByUser(user);

        return GetAccountResDto.fromEntity(account);
    }
}
