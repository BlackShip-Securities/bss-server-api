package com.bss.bssserverapi.domain.account.dto;

import com.bss.bssserverapi.domain.account.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class GetAccountResDto {

    private BigDecimal balance;

    public static GetAccountResDto fromEntity(final Account account) {

        return new GetAccountResDto(account.getBalance());
    }
}
