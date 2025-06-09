package com.bss.bssserverapi.global.websocket.binance.service;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.account.repository.AccountJpaRepository;
import com.bss.bssserverapi.domain.account.service.AccountService;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.crypto.repository.CryptoJpaRepository;
import com.bss.bssserverapi.domain.holding.Holding;
import com.bss.bssserverapi.domain.holding.repository.HoldingJpaRepository;
import com.bss.bssserverapi.domain.order.Order;
import com.bss.bssserverapi.domain.order.StatusType;
import com.bss.bssserverapi.domain.order.dto.InMemoryOrderDto;
import com.bss.bssserverapi.domain.order.repository.InMemoryVirtualOrderBookRepository;
import com.bss.bssserverapi.domain.order.repository.OrderJpaRepository;
import com.bss.bssserverapi.domain.trade.Trade;
import com.bss.bssserverapi.domain.trade.repository.TradeJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderMatchingService {

    private final InMemoryVirtualOrderBookRepository inMemoryVirtualOrderBookRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final CryptoJpaRepository cryptoJpaRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final TradeJpaRepository tradeJpaRepository;
    private final HoldingJpaRepository holdingJpaRepository;
    private final AccountService accountService;

    @Transactional
    public void executeMatching(final String symbol, final BigDecimal orderPrice, final BigDecimal orderQuantity) {

        final Crypto crypto = cryptoJpaRepository.findBySymbol(symbol).orElse(null);
        if (crypto == null) {
            log.warn("[OrderMatching] Symbol not found: {}", symbol);
            return;
        }

        for (final Long accountId : inMemoryVirtualOrderBookRepository.findAccountIdsByCryptoId(crypto.getId())) {

            final Account account = accountJpaRepository.findByIdForUpdate(accountId);
            if (account == null) {
                log.warn("[OrderMatching] Account not found: id={}", accountId);
                continue;
            }

            matchingBids(crypto, account, orderPrice, orderQuantity);
            matchingAsks(crypto, account, orderPrice, orderQuantity);
        }
    }

    private void matchingBids(final Crypto crypto, final Account account, final BigDecimal orderPrice, BigDecimal remainingOrderQty) {

        final Iterator<InMemoryOrderDto> iterator = inMemoryVirtualOrderBookRepository.findBidsByCryptoAndAccount(crypto, account).iterator();

        // match bids
        while (iterator.hasNext() && remainingOrderQty.compareTo(BigDecimal.ZERO) > 0) {

            final InMemoryOrderDto bidOrder = iterator.next();
            if (bidOrder.getPrice().compareTo(orderPrice) < 0) break;

            final BigDecimal bidQuantity = bidOrder.getQty();
            final BigDecimal executedQty = bidQuantity.min(remainingOrderQty);
            remainingOrderQty = remainingOrderQty.subtract(executedQty);

            final Order order = orderJpaRepository.findById(bidOrder.orderId).orElse(null);
            if (order == null) {
                log.warn("[OrderMatching] Order not found: id={}", bidOrder.orderId);
                continue;
            }

            final Trade trade = Trade.builder()
                    .sideType(order.getSideType())
                    .price(bidOrder.getPrice())
                    .quantity(executedQty)
                    .amount(bidOrder.getPrice().multiply(executedQty))
                    .fee(BigDecimal.ZERO)
                    .build();

            final Holding holding = holdingJpaRepository.findByAccountAndCrypto(account, crypto)
                    .orElseGet(Holding::new);

            holding.addTrade(trade);
            account.addHolding(holding);
            order.addTrade(trade);
            account.addTrade(trade);
            trade.setCrypto(crypto);

            holdingJpaRepository.save(holding);
            tradeJpaRepository.save(trade);
            orderJpaRepository.save(order);
            accountJpaRepository.save(account);

            if (bidQuantity.compareTo(executedQty) > 0) {
                bidOrder.subtractQty(executedQty);
            } else {
                iterator.remove();
            }
        }
    }

    // match asks
    private void matchingAsks(final Crypto crypto, final Account account, final BigDecimal orderPrice, BigDecimal remainingOrderQty) {

        final Iterator<InMemoryOrderDto> iterator = inMemoryVirtualOrderBookRepository.findAsksByCryptoAndAccount(crypto, account).iterator();

        while (iterator.hasNext() && remainingOrderQty.compareTo(BigDecimal.ZERO) > 0) {

            final InMemoryOrderDto asksOrder = iterator.next();
            if (asksOrder.getPrice().compareTo(orderPrice) > 0) break;

            final BigDecimal orderQty = asksOrder.getQty();
            final BigDecimal executedQty = orderQty.min(remainingOrderQty);
            remainingOrderQty = remainingOrderQty.subtract(executedQty);

            final Order order = orderJpaRepository.findById(asksOrder.orderId).orElse(null);
            if (order == null) {
                log.warn("[OrderMatching] Order not found: id={}", asksOrder.orderId);
                continue;
            }

            final Trade trade = Trade.builder()
                    .sideType(order.getSideType())
                    .price(asksOrder.getPrice())
                    .quantity(executedQty)
                    .amount(asksOrder.getPrice().multiply(executedQty))
                    .fee(BigDecimal.ZERO)
                    .build();

            final Holding holding = holdingJpaRepository.findByAccountAndCrypto(account, crypto).orElse(null);
            if(holding == null) {
                log.warn("[OrderMatching] holding not found: id={}", asksOrder.orderId);
                continue;
            }

            // TODO: 홀딩에서 매수 체결 내역을 빼야함
            holding.addTrade(trade.getQuantity(), trade.getPrice());

            order.addTrade(trade);
            if (order.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                order.updateStatusType(StatusType.MATCHED);
            } else {
                order.updateStatusType(StatusType.PARTIALLY_MATCHED);
            }
            account.addTrade(trade);
            trade.setCrypto(crypto);

            holdingJpaRepository.save(holding);
            tradeJpaRepository.save(trade);
            orderJpaRepository.save(order);
            accountJpaRepository.save(account);

            if (orderQty.compareTo(executedQty) > 0) {
                asksOrder.subtractQty(executedQty);
            } else {
                iterator.remove();
            }
        }
    }
}
