package com.bss.bssserverapi.domain.order.service;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.account.repository.AccountJpaRepository;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.crypto.repository.CryptoJpaRepository;
import com.bss.bssserverapi.domain.holding.Holding;
import com.bss.bssserverapi.domain.holding.repository.HoldingJpaRepository;
import com.bss.bssserverapi.domain.order.Order;
import com.bss.bssserverapi.domain.order.OrderType;
import com.bss.bssserverapi.domain.order.SideType;
import com.bss.bssserverapi.domain.order.StatusType;
import com.bss.bssserverapi.domain.order.dto.CreateSpotOrderByMarketReqDto;
import com.bss.bssserverapi.domain.order.repository.OrderJpaRepository;
import com.bss.bssserverapi.domain.trade.Trade;
import com.bss.bssserverapi.domain.user.User;
import com.bss.bssserverapi.domain.user.repository.UserJpaRepository;
import com.bss.bssserverapi.global.exception.ErrorCode;
import com.bss.bssserverapi.global.exception.GlobalException;
import com.bss.bssserverapi.global.websocket.binance.repository.InMemoryOrderBookRepository;
import com.bss.bssserverapi.global.websocket.binance.repository.OrderBook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NavigableMap;

@Service
@RequiredArgsConstructor
public class OrderByMarketService {

    private final UserJpaRepository userJpaRepository;
    private final CryptoJpaRepository cryptoJpaRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final InMemoryOrderBookRepository inMemoryOrderBookRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final HoldingJpaRepository holdingJpaRepository;

    private final int LIMIT = 20;

    @Transactional
    public void createSpotOrderByMarket(final String userName, final CreateSpotOrderByMarketReqDto req) {

        final User user = userJpaRepository.findByUserName(userName)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        final Crypto crypto = cryptoJpaRepository.findBySymbol(req.getSymbol())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, ErrorCode.CRYPTO_NOT_FOUND));

        final OrderBook orderBook = inMemoryOrderBookRepository.findOrSaveBySymbol(crypto.getSymbol());

        final Account account = accountJpaRepository.findAccountByUser(user);

        final Holding holding = this.getOrSaveHolding(account, crypto);

        final SideType sideType = req.getSideType();

        switch (sideType) {
            case LONG:
                this.createSpotLongOrderByMarket(orderBook, account, holding, crypto, req.getQty());
                break;
            case SHORT:
                this.createSpotShortOrderByMarket(orderBook, account, holding, crypto, req.getQty());
                break;
            default:
                throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.UNSUPPORTED_SIDE_TYPE);
        }
    }

    private Holding getOrSaveHolding(final Account account, final Crypto crypto) {

        return holdingJpaRepository.findByAccountAndCrypto(account, crypto)
                .orElseGet(() -> holdingJpaRepository.save(new Holding(account, crypto)));
    }

    private void createSpotLongOrderByMarket(final OrderBook orderBook, final Account account, final Holding holding, final Crypto crypto, final BigDecimal qty) {

        final NavigableMap<BigDecimal, BigDecimal> asks = orderBook.getAsks();

        this.checkAsksLiquidity(account.getBalance(), qty, asks);

        this.matchSpotLongOrder(qty, account, holding, crypto, asks);
    }

    public void createSpotShortOrderByMarket(final OrderBook orderBook, final Account account, final Holding holding, final Crypto crypto, final BigDecimal qty) {

        final NavigableMap<BigDecimal, BigDecimal> bids = orderBook.getBids();

        this.checkBidsLiquidity(holding, qty, bids);

        this.matchSpotShortOrder(qty, account, holding, crypto, bids);
    }

    private void checkAsksLiquidity(final BigDecimal balance, final BigDecimal qty, final NavigableMap<BigDecimal, BigDecimal> asks) {

        BigDecimal remainingQty = qty;
        BigDecimal totalCost = BigDecimal.ZERO;
        int depth = 0;

        for(final var entry : asks.entrySet()){
            if(depth >= LIMIT || remainingQty.compareTo(BigDecimal.ZERO) == 0)  break;

            final BigDecimal price = entry.getKey();
            final BigDecimal availableQty = entry.getValue();

            final BigDecimal tradeQty = remainingQty.min(availableQty);
            final BigDecimal cost = price.multiply(tradeQty);

            totalCost = totalCost.add(cost);
            remainingQty = remainingQty.subtract(tradeQty);

            depth++;
        }

        // 계좌 잔고 부족
        if(balance.compareTo(totalCost) < 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 20depth 의 호가로 매수 요청을 체결할 수 없는 경우
        if(remainingQty.compareTo(BigDecimal.ZERO) > 0){
            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_ENOUGH_ORDERBOOK_LIQUIDITY);
        }
    }

    private void checkBidsLiquidity(final Holding holding, final BigDecimal qty, final NavigableMap<BigDecimal, BigDecimal> asks) {

        // 보유 수량을 초과하는 매도 요청
        if(holding.getQuantity().compareTo(qty) < 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.INSUFFICIENT_QUANTITY);
        }

        BigDecimal remainingQty = qty;
        int depth = 0;

        for(final var entry : asks.entrySet()){
            if(depth >= LIMIT || remainingQty.compareTo(BigDecimal.ZERO) == 0)  break;

            final BigDecimal availableQty = entry.getValue();

            final BigDecimal tradeQty = remainingQty.min(availableQty);

            remainingQty = remainingQty.subtract(tradeQty);

            depth++;
        }

        // 20depth 의 호가로 매도 요청을 체결할 수 없는 경우
        if(remainingQty.compareTo(BigDecimal.ZERO) > 0){
            throw new GlobalException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_ENOUGH_ORDERBOOK_LIQUIDITY);
        }
    }

    private void matchSpotLongOrder(final BigDecimal qty, final Account account, final Holding holding, final Crypto crypto, final NavigableMap<BigDecimal, BigDecimal> asks) {

        // 주문 생성
        final Order order = Order.builder()
                .sideType(SideType.LONG)
                .orderType(OrderType.MARKET)
                .statusType(StatusType.NEW)
                .price(BigDecimal.ZERO) // TODO: 이론상 최대값으로 해놓거나, Entity 분리
                .quantity(qty)
                .remainingQuantity(qty)
                .build();

        account.addOrder(order);
        order.setCrypto(crypto);

        // 거래 체결
        BigDecimal remainingQty = qty;
        BigDecimal totalCost = BigDecimal.ZERO;
        int depth = 0;
        for(final var entry : asks.entrySet()){
            if(depth >= LIMIT || remainingQty.compareTo(BigDecimal.ZERO) == 0)  break;

            final BigDecimal price = entry.getKey();
            final BigDecimal availableQty = entry.getValue();

            final BigDecimal tradeQty = remainingQty.min(availableQty);
            final BigDecimal cost = price.multiply(tradeQty);

            totalCost = totalCost.add(cost);
            remainingQty = remainingQty.subtract(tradeQty);

            final Trade trade = Trade.builder()
                    .sideType(SideType.LONG)
                    .price(price)
                    .quantity(tradeQty)
                    .amount(cost)
                    .fee(BigDecimal.ZERO)
                    .build();

            order.addTrade(trade);
            account.addTrade(trade);
            trade.setCrypto(crypto);

            holding.applyLongTrade(tradeQty, cost);

            depth++;
        }

        // 거래 체결(매칭 완료)
        order.updateStatusType(StatusType.MATCHED);

        orderJpaRepository.save(order);
    }

    private void matchSpotShortOrder(final BigDecimal qty, final Account account, final Holding holding, final Crypto crypto, final NavigableMap<BigDecimal, BigDecimal> asks) {

        // 주문 생성
        final Order order = Order.builder()
                .sideType(SideType.SHORT)
                .orderType(OrderType.MARKET)
                .statusType(StatusType.NEW)
                .price(BigDecimal.ZERO) // TODO: 이론상 최대값으로 해놓거나, Entity 분리
                .quantity(qty)
                .remainingQuantity(qty)
                .build();

        account.addOrder(order);
        order.setCrypto(crypto);

        // 거래 체결
        BigDecimal remainingQty = qty;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int depth = 0;

        for(final var entry : asks.entrySet()){
            if(depth >= LIMIT || remainingQty.compareTo(BigDecimal.ZERO) == 0)  break;

            final BigDecimal price = entry.getKey();
            final BigDecimal availableQty = entry.getValue();

            final BigDecimal tradeQty = remainingQty.min(availableQty);
            final BigDecimal revenue = price.multiply(tradeQty);

            totalRevenue = totalRevenue.add(revenue);
            remainingQty = remainingQty.subtract(tradeQty);

            final Trade trade = Trade.builder()
                    .sideType(SideType.SHORT)
                    .price(price)
                    .quantity(tradeQty)
                    .amount(revenue)
                    .fee(BigDecimal.ZERO)
                    .build();

            order.addTrade(trade);
            account.addTrade(trade);
            trade.setCrypto(crypto);

            holding.applyShortTrade(tradeQty, revenue);

            depth++;
        }

        // 거래 체결(매칭 완료)
        order.updateStatusType(StatusType.MATCHED);

        orderJpaRepository.save(order);
    }
}
