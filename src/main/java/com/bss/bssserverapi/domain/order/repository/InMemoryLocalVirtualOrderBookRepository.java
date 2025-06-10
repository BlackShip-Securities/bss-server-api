//package com.bss.bssserverapi.domain.order.repository;
//
//import com.bss.bssserverapi.domain.account.Account;
//import com.bss.bssserverapi.domain.crypto.Crypto;
//import com.bss.bssserverapi.domain.order.dto.InMemoryOrderDto;
//import org.springframework.stereotype.Repository;
//
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Repository
//public class InMemoryLocalVirtualOrderBookRepository implements InMemoryVirtualOrderBookRepository {
//
//    private final Map<String, VirtualOrderBook> virtualOrderBookMap = new HashMap<>();
//
//    public Set<Long> findAccountIdsByCryptoId(final Long cryptoId) {
//
//        final String prefix = cryptoId + "-";
//        return virtualOrderBookMap.keySet().stream()
//                .filter(key -> key.startsWith(prefix))
//                .map(key -> {
//                    final String[] parts = key.split("-");
//                    return Long.parseLong(parts[1]);
//                })
//                .collect(Collectors.toSet());
//    }
//
//    @Override
//    public NavigableSet<InMemoryOrderDto> findAsksByCryptoAndAccount(final Crypto crypto, final Account account) {
//
//        return getOrCreate(crypto.getId(), account.getId()).getAsks();
//    }
//
//    @Override
//    public NavigableSet<InMemoryOrderDto> findBidsByCryptoAndAccount(final Crypto crypto, final Account account) {
//
//        return getOrCreate(crypto.getId(), account.getId()).getBids();
//    }
//
//    @Override
//    public void addAskByCryptoAndAccount(final Crypto crypto, final Account account, final InMemoryOrderDto order) {
//
//        getOrCreate(crypto.getId(), account.getId()).getAsks().add(order);
//    }
//
//    @Override
//    public void addBidByCryptoAndAccount(final Crypto crypto, final Account account, final InMemoryOrderDto order) {
//
//        getOrCreate(crypto.getId(), account.getId()).getBids().add(order);
//    }
//
//    @Override
//    public void decrementAskQuantity(final Long cryptoId, final Long accountId, final Long orderId, final BigDecimal price, final BigDecimal executeQuantity) {
//
//    }
//
//    @Override
//    public void decrementBidQuantity(final Long cryptoId, final Long accountId, final Long orderId, final BigDecimal price, final BigDecimal executeQuantity) {
//
//    }
//
//    private VirtualOrderBook getOrCreate(final Long cryptoId, final Long accountId) {
//
//        final String key = cryptoId + "-" + accountId;
//        return virtualOrderBookMap.computeIfAbsent(key, k -> new VirtualOrderBook());
//    }
//}