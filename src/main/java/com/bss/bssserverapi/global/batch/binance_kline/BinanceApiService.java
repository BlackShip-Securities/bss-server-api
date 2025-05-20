package com.bss.bssserverapi.global.batch.binance_kline;

import com.bss.bssserverapi.domain.kline.Kline;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BinanceApiService {

    public List<Kline> fetchKlineList(final String symbol, final String interval, final Long startTime, final Long limit) {

        return List.of();
    }
}
