package com.bss.bssserverapi.global.batch.binance_kline;

import com.bss.bssserverapi.domain.kline.Kline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BinanceApiService {

    private static final String BASE_URL = "https://api.binance.com/api/v3/klines";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Kline> fetchKlineList(final String symbol, final String interval, final Long startTime, final Long endTime, final Long limit) {

        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("symbol", symbol)
                .queryParam("interval", interval)
                .queryParam("startTime", startTime)
                .queryParam("endTime", endTime)
                .queryParam("limit", limit)
                .toUriString();

        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            List<List<Object>> rawKlineList = response.getBody();

            if (rawKlineList == null || rawKlineList.isEmpty()) return List.of();

            List<Kline> result = new ArrayList<>();
            for (List<Object> raw : rawKlineList) {
                Kline kline = new Kline(
                        symbol,
                        interval,
                        ((Number) raw.get(0)).longValue(),  // openTime
                        ((Number) raw.get(6)).longValue(),  // closeTime
                        new BigDecimal((String) raw.get(1)), // openPrice
                        new BigDecimal((String) raw.get(4)), // closePrice
                        new BigDecimal((String) raw.get(2)), // highPrice
                        new BigDecimal((String) raw.get(3)), // lowPrice
                        new BigDecimal((String) raw.get(5)), // baseVolume
                        ((Number) raw.get(8)).intValue(),    // tradeCount
                        new BigDecimal((String) raw.get(7)), // quoteVolume
                        new BigDecimal((String) raw.get(9)), // takerBuyBaseVolume
                        new BigDecimal((String) raw.get(10)) // takerBuyQuoteVolume
                );
                result.add(kline);
            }

            return result;

        } catch (Exception e) {
            log.error("Failed to fetch kline for symbol={} interval={} start={} end={}", symbol, interval, startTime, endTime, e);
            return List.of();
        }
    }

    public Long fetchEarliestKlineOpenTime(final String symbol, final String interval) {

        List<Kline> klineList = fetchKlineList(symbol, interval, 0L, System.currentTimeMillis(), 1L);

        if (klineList.isEmpty())
            throw new IllegalStateException("No kline found for symbol=" + symbol);

        return klineList.get(0).getOpenTime();
    }
}