package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.global.websocket.binance.BinanceRedisTopicType;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceDepthMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinanceDepthMessageHandler implements BinanceMessageHandler<BinanceDepthMessage> {

    private final RedissonClient redissonClient;

    @Override
    public void handle(final BinanceRedisTopicType topicType, final BinanceDepthMessage message) {


    }

    @Override
    public Class<BinanceDepthMessage> getMessageType() {

        return BinanceDepthMessage.class;
    }
}