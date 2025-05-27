package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.global.websocket.binance.BinanceRedisTopicType;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceDepthMessage;
import com.bss.bssserverapi.global.websocket.binance.repository.InMemoryOrderBookBufferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinanceDepthMessageHandler implements BinanceMessageHandler<BinanceDepthMessage> {

    private final RedissonClient redissonClient;
    private final InMemoryOrderBookBufferRepository orderBookBufferRepository;

    @Override
    public void handle(final BinanceRedisTopicType topicType, final BinanceDepthMessage message) {

//        log.info("Received depth update for symbol={}, U={}, u={}", message.getSymbol(), message.getFirstUpdateId(), message.getFinalUpdateId());
        orderBookBufferRepository.add(message);
    }

    @Override
    public Class<BinanceDepthMessage> getMessageType() {

        return BinanceDepthMessage.class;
    }
}