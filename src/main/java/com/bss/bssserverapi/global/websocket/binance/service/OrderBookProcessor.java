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

    private static final Long TIME_SLEEP = 100L;

    @EventListener(ApplicationReadyEvent.class)
    public void consume() throws InterruptedException{

        while(true){
            for(final String symbol : orderBookBufferRepository.getSymbols().stream().toList()) {
                if(orderBookBufferRepository.isEmptyBySymbol(symbol)){
                    Thread.sleep(TIME_SLEEP);
                    continue;
                }

                final BinanceDepthMessage message = orderBookBufferRepository.pollBySymbol(symbol);
                final OrderBook orderBook = orderBookRepository.findOrSaveBySymbol(message.getSymbol());

                if (orderBook.getLastUpdateId() + 1 < message.getFirstUpdateId()){
                    log.info("[process] {} {} {}", orderBook.getLastUpdateId(), message.getFirstUpdateId(), message.getFinalUpdateId());
                    this.process(orderBook);
                } else {
//                    log.info("[update] {} {} {}", orderBook.getLastUpdateId(), message.getFirstUpdateId(), message.getFinalUpdateId());
                    this.update(orderBook, message.getFinalUpdateId(), message.getAsks(), message.getBids());
                }

                this.publish(orderBook);
            }
        }
    }

    private void process(final OrderBook orderBook) throws InterruptedException{

        final String symbol = orderBook.getSymbol();
        final BinanceOrderBookSnapshot snapshot = binanceApiService.fetchOrderBookSnapshot(symbol, 20);
        final Long lastUpdateId = snapshot.getLastUpdateId();

        Thread.sleep(TIME_SLEEP);

        while (!orderBookBufferRepository.isEmptyBySymbol(symbol)){
            final BinanceDepthMessage message = orderBookBufferRepository.pollBySymbol(symbol);

            if (message.getFinalUpdateId() <= lastUpdateId)
                continue;
            orderBook.clear();
            update(orderBook, snapshot.getLastUpdateId(), snapshot.getAsks(), snapshot.getBids());
            update(orderBook, message.getFinalUpdateId(), message.getAsks(), message.getBids());
        }
    }

    private void update(final OrderBook orderBook, final Long lastUpdateId, final List<List<String>> asks, final List<List<String>> bids){

        orderBook.setLastUpdateId(lastUpdateId);

        // asks
        for (final List<String> ask : asks) {
            final BigDecimal price = new BigDecimal(ask.get(0));
            final BigDecimal quantity = new BigDecimal(ask.get(1));

            orderBook.updateAsk(price, quantity);
        }

        // bids
        for (final List<String> bid : bids) {
            final BigDecimal price = new BigDecimal(bid.get(0));
            final BigDecimal quantity = new BigDecimal(bid.get(1));

            orderBook.updateBid(price, quantity);
        }
    }

    private void publish(final OrderBook orderBook) {

        final String redisTopic = "crypto/depth/" + orderBook.getSymbol().toLowerCase();
        final OrderBookDto orderBookDto = OrderBookDto.from(orderBook, 10L);
        this.redissonClient.getTopic(redisTopic).publish(orderBookDto);
    }
}
