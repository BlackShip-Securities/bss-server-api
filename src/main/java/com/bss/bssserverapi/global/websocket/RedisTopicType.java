package com.bss.bssserverapi.global.websocket;

import com.bss.bssserverapi.global.websocket.dto.KlineMessage;
import com.bss.bssserverapi.global.websocket.dto.TickerMessage;
import com.bss.bssserverapi.global.websocket.dto.TradeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum RedisTopicType {

    KLINE("kline", "crypto/kline/", KlineMessage.class),
    TICKER("24hrTicker", "crypto/ticker/", TickerMessage.class),
    TRADE("trade", "crypto/trade/", TradeMessage.class);

    private final String streamName;
    private final String redisPrefix;
    private final Class<?> messageType;

    public static Optional<RedisTopicType> fromStreamName(final String streamName){

        return Arrays.stream(values())
                .filter(t -> t.streamName.equals(streamName))
                .findFirst();
    }
}
