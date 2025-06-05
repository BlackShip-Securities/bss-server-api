package com.bss.bssserverapi.domain.order.service;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.account.repository.AccountJpaRepository;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.crypto.repository.CryptoJpaRepository;
import com.bss.bssserverapi.domain.holding.repository.HoldingJpaRepository;
import com.bss.bssserverapi.domain.order.Order;
import com.bss.bssserverapi.domain.order.OrderType;
import com.bss.bssserverapi.domain.order.SideType;
import com.bss.bssserverapi.domain.order.StatusType;
import com.bss.bssserverapi.domain.order.dto.CreateSpotOrderByLimitReqDto;
import com.bss.bssserverapi.domain.order.dto.InMemoryOrderDto;
import com.bss.bssserverapi.domain.order.repository.InMemoryVirtualOrderBookRepository;
import com.bss.bssserverapi.domain.order.repository.OrderJpaRepository;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import com.bss.bssserverapi.global.websocket.binance.repository.InMemoryOrderBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NavigableSet;

@Service
@RequiredArgsConstructor
public class OrderByLimitService {

    private final UserJpaRepository userJpaRepository;
    private final CryptoJpaRepository cryptoJpaRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final InMemoryVirtualOrderBookRepository inMemoryVirtualOrderBookRepository;

    @Transactional
    public void createSpotOrderByLimit(final String userName, final CreateSpotOrderByLimitReqDto req) {

        final User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        final Crypto crypto = cryptoJpaRepository.findBySymbol(req.getSymbol())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.CRYPTO_NOT_FOUND));

        final Account account = accountJpaRepository.findAccountByUser(user);

        final SideType sideType = req.getSideType();

        switch (sideType) {
            case LONG:
                this.createSpotLongOrderByLimit(account, crypto, req.getPrice(), req.getQty());
                break;
            case SHORT:
                break;
            default:
                throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.UNSUPPORTED_SIDE_TYPE);
        }
    }

    private void createSpotLongOrderByLimit(final Account account, final Crypto crypto, final BigDecimal price, final BigDecimal qty) {

        this.checkBalance(account, price, qty);

        final Order order = Order.builder()
                .sideType(SideType.LONG)
                .orderType(OrderType.LIMIT)
                .statusType(StatusType.NEW)
                .price(price)
                .quantity(qty)
                .remainingQuantity(qty)
                .build();

        account.addOrder(order);
        order.setCrypto(crypto);

        orderJpaRepository.save(order);

        inMemoryVirtualOrderBookRepository.addBidByCryptoAndAccount(crypto, account, InMemoryOrderDto.fromEntity(order));
    }

    private void checkBalance(final Account account, final BigDecimal price, final BigDecimal qty) {

        final BigDecimal balance = account.getBalance();
        final BigDecimal cost = price.multiply(qty);

        if(balance.compareTo(cost) < 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.INSUFFICIENT_BALANCE);
        }
    }

    private void createSpotShortOrderByLimit() {

    }
}
