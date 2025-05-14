package com.bss.bssserverapi.global.websocket.binance.handler;

import com.bss.bssserverapi.domain.kline.Kline;
import com.bss.bssserverapi.domain.kline.repository.KlineJpaRepository;
import com.bss.bssserverapi.global.websocket.binance.dto.BinanceKlineMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinanceKlineMessageHandler implements BinanceMessageHandler<BinanceKlineMessage> {

    private final KlineJpaRepository klineJpaRepository;

    @Override
    public void handle(final BinanceKlineMessage message) {

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
