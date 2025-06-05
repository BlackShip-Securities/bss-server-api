package com.bss.bssserverapi.domain.order.repository;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.order.dto.InMemoryOrderDto;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryLocalVirtualOrderBookRepository implements InMemoryVirtualOrderBookRepository {

    private final Map<String, VirtualOrderBook> virtualOrderBookMap = new HashMap<>();

    @Override
    public NavigableSet<InMemoryOrderDto> findAsksByCryptoAndAccount(final Crypto crypto, final Account account) {

        return getOrCreate(crypto.getId(), account.getId()).getAsks().stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(InMemoryOrderDto::getPrice)
                                .thenComparing(InMemoryOrderDto::getOrderId)
                )));
    }

    @Override
    public NavigableSet<InMemoryOrderDto> findBidsByCryptoAndAccount(final Crypto crypto, final Account account) {

        return getOrCreate(crypto.getId(), account.getId()).getBids().stream()
                .collect(Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(InMemoryOrderDto::getPrice).reversed()
                                .thenComparing(InMemoryOrderDto::getOrderId)
                )));
    }

    @Override
    public void addAskByCryptoAndAccount(final Crypto crypto, final Account account, final InMemoryOrderDto order) {

        getOrCreate(crypto.getId(), account.getId()).getAsks().add(order);
    }

    @Override
    public void addBidByCryptoAndAccount(final Crypto crypto, final Account account, final InMemoryOrderDto order) {

        getOrCreate(crypto.getId(), account.getId()).getBids().add(order);
    }

    private VirtualOrderBook getOrCreate(final Long cryptoId, final Long accountId) {

        final String key = cryptoId + "-" + accountId;
        return virtualOrderBookMap.computeIfAbsent(key, k -> new VirtualOrderBook());
    }
}