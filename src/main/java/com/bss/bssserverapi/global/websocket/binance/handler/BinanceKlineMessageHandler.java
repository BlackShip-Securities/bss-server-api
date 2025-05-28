package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.domain.kline.Kline;
import com.bss.bssserverapi.domain.kline.repository.KlineJpaRepository;
import com.bss.bssserverapi.global.websocket.binance.BinanceRedisTopicType;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceKlineMessage;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceKlineMessageHandler implements BinanceMessageHandler<BinanceKlineMessage> {

    private final KlineJpaRepository klineJpaRepository;
    private final RedissonClient redissonClient;

    @Override
    public void handle(final BinanceRedisTopicType topicType, final BinanceKlineMessage message) {

        // redis publish
        String redisTopic = topicType.getRedisPrefix() + message.getSymbol().toLowerCase();
        this.redissonClient.getTopic(redisTopic).publish(message);

        if(message.getKline().isClosed()) {
            Kline kline = Kline.fromBinanceKlineMessage(message);
            klineJpaRepository.save(kline);
        }
    }

    @Override
    public Class<BinanceKlineMessage> getMessageType() {

        return BinanceKlineMessage.class;
    }
}
