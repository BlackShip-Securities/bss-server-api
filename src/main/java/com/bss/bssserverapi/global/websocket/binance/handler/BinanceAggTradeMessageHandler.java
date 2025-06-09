package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.global.websocket.binance.BinanceRedisTopicType;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceAggTradeMessage;
import com.bss.bssserverapi.global.websocket.binance.service.TradeAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinanceAggTradeMessageHandler implements BinanceMessageHandler<BinanceAggTradeMessage> {

    private final TradeAggregator tradeAggregator;

    @Override
    public void handle(final BinanceRedisTopicType topicType, final BinanceAggTradeMessage message) {

        tradeAggregator.accumulate(message);
    }

    @Override
    public Class<BinanceAggTradeMessage> getMessageType() {

        return BinanceAggTradeMessage.class;
    }
}
