package com.bss.bssserverapi.global.batch.binance_kline;

import com.bss.bssserverapi.domain.kline.Kline;
import com.bss.bssserverapi.domain.kline.repository.KlineJpaRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class KlineReader implements ItemReader<Kline> {

    private final BinanceApiService binanceApiService;

    private final Queue<KlineFetchRequest> requestQueue = new LinkedList<>();
    private final Queue<Kline> buffer = new LinkedList<>();

    public KlineReader(final BinanceApiService binanceApiService, final KlineJpaRepository klineJpaRepository) throws InterruptedException {

        final long LIMIT = 1000L;
        final long BATCH_END_TIME = System.currentTimeMillis();

        this.binanceApiService = binanceApiService;

        List<String> symbols = List.of("BTCUSDT", "ETHUSDT");
        List<String> intervals = List.of("1m", "3m", "5m", "15m", "30m", "1h", "1d", "1w", "1M");

        for (String symbol : symbols) {
            for (String interval : intervals) {
                Long intervalMillis = getIntervalMillis(interval);
                Long startTime = klineJpaRepository.findLatestOpenTime(symbol, interval)
                        .map(openTime -> openTime + intervalMillis)
                        .orElse(binanceApiService.fetchEarliestKlineOpenTime(symbol, interval));

                long diff = LIMIT * intervalMillis;
                for(long t = startTime; t < BATCH_END_TIME; t += diff){
                    requestQueue.add(new KlineFetchRequest(symbol, interval, t, t + diff - 1));
                }
            }

            Thread.sleep(1000);
        }
    }

    @Override
    public Kline read() throws Exception {

        while (buffer.isEmpty() && !requestQueue.isEmpty()) {
            fillBuffer(requestQueue.poll());
        }

        return buffer.poll();
    }

    private void fillBuffer(final KlineFetchRequest request) throws InterruptedException {

        List<Kline> klineList = binanceApiService.fetchKlineList(request.symbol(), request.interval(), request.startTime(), request.endTime(), 1000L);
        if (klineList.isEmpty()) return;

        buffer.addAll(klineList);

        Thread.sleep(1000);
    }

    private Long getIntervalMillis(final String interval) {

        return switch (interval) {
            case "1m" -> 60_000L;
            case "3m" -> 180_000L;
            case "5m" -> 300_000L;
            case "15m" -> 900_000L;
            case "30m" -> 1_800_000L;
            case "1h" -> 3_600_000L;
            case "1d" -> 86_400_000L;
            case "1w" -> 604_800_000L;
            case "1M" -> 2_592_000_000L;
            default -> throw new IllegalArgumentException("Unsupported interval: " + interval);
        };
    }
}
