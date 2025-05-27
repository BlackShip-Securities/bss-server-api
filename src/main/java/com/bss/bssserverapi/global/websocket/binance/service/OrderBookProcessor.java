package com.bss.bssserverapi.global.websocket.binance.service;

import com.bss.bssserverapi.global.external.BinanceApiService;
import com.bss.bssserverapi.global.external.dto.BinanceOrderBookSnapshot;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceDepthMessage;
import com.bss.bssserverapi.global.websocket.binance.dto.OrderBookDto;
import com.bss.bssserverapi.global.websocket.binance.repository.InMemoryOrderBookBufferRepository;
import com.bss.bssserverapi.global.websocket.binance.repository.InMemoryOrderBookRepository;
import com.bss.bssserverapi.global.websocket.binance.repository.OrderBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderBookProcessor {

    private final InMemoryOrderBookBufferRepository orderBookBufferRepository;
    private final InMemoryOrderBookRepository orderBookRepository;
    private final BinanceApiService binanceApiService;
    private final RedissonClient redissonClient;

    @EventListener(ApplicationReadyEvent.class)
    public void consume() throws InterruptedException{

        while(true){
            for(final String symbol : orderBookBufferRepository.getSymbols()) {
                if(orderBookBufferRepository.isEmptyBySymbol(symbol)){
                    Thread.sleep(20);
                    continue;
                }

                final BinanceDepthMessage message = orderBookBufferRepository.pollBySymbol(symbol);

                final OrderBook orderBook = orderBookRepository.findOrSaveBySymbol(message.getSymbol());
                log.info("[OrderBook] symbol={} LastUpdateId={} U={} u={}", symbol, orderBook.getLastUpdateId(), message.getFirstUpdateId(), message.getFinalUpdateId());
                if (orderBook.getLastUpdateId() + 1 < message.getFirstUpdateId())
                    process(orderBook);
                else
                    update(orderBook, message.getFinalUpdateId(), message.getBids(), message.getAsks());

                publish(orderBook);
            }
        }
    }

    private void process(final OrderBook orderBook){

        final String symbol = orderBook.getSymbol();
        final BinanceOrderBookSnapshot snapshot = binanceApiService.fetchOrderBookSnapshot(symbol, 5000);
        final Long lastUpdateId = snapshot.getLastUpdateId();

        while (!orderBookBufferRepository.isEmptyBySymbol(symbol)){
            final BinanceDepthMessage message = orderBookBufferRepository.pollBySymbol(symbol);
            log.info("[process] symbol={} LastUpdateId={} U={} u={}", symbol, snapshot.getLastUpdateId(), message.getFirstUpdateId(), message.getFinalUpdateId());
            if (message.getFinalUpdateId() <= lastUpdateId)
                continue;

            orderBook.clear();
            update(orderBook, snapshot.getLastUpdateId(), snapshot.getBids(), snapshot.getAsks());
            update(orderBook, message.getFinalUpdateId(), message.getBids(), message.getAsks());
        }
    }

    private void update(final OrderBook orderBook, final Long lastUpdateId, final List<List<String>> bids, final List<List<String>> asks){

        orderBook.setLastUpdateId(lastUpdateId);

        // bids
        for (final List<String> bid : bids) {
            final BigDecimal price = new BigDecimal(bid.get(0));
            final BigDecimal quantity = new BigDecimal(bid.get(1));

            if (quantity.compareTo(BigDecimal.ZERO) == 0) {
                orderBook.getBids().remove(price);
            } else {
                orderBook.getBids().put(price, quantity);
            }
        }

        // asks
        for (final List<String> ask : asks) {
            final BigDecimal price = new BigDecimal(ask.get(0));
            final BigDecimal quantity = new BigDecimal(ask.get(1));

            if (quantity.compareTo(BigDecimal.ZERO) == 0) {
                orderBook.getAsks().remove(price);
            } else {
                orderBook.getAsks().put(price, quantity);
            }
        }
    }

    private void publish(final OrderBook orderBook) {

        final String redisTopic = "crypto/depth/" + orderBook.getSymbol().toLowerCase();
        final OrderBookDto orderBookDto = OrderBookDto.from(orderBook, 10L);
        this.redissonClient.getTopic(redisTopic).publish(orderBookDto);
    }
}
