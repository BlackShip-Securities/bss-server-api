package com.bss.bssserverapi.domain.order.repository;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.order.dto.InMemoryOrderDto;

import java.util.NavigableSet;
import java.util.Set;

public interface InMemoryVirtualOrderBookRepository {

    Set<String> keySet();

    Set<Long> findAccountIdsByCryptoId(final Long cryptoId);

    NavigableSet<InMemoryOrderDto> findAsksByCryptoAndAccount(final Crypto crypto, final Account account);

    NavigableSet<InMemoryOrderDto> findBidsByCryptoAndAccount(final Crypto crypto, final Account account);

    void addAskByCryptoAndAccount(final Crypto crypto, final Account account, final InMemoryOrderDto order);

    void addBidByCryptoAndAccount(final Crypto crypto, final Account account, final InMemoryOrderDto order);
}