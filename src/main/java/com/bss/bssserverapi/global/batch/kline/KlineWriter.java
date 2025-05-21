package com.bss.bssserverapi.global.batch.kline;

import com.bss.bssserverapi.domain.kline.Kline;
import com.bss.bssserverapi.domain.kline.repository.KlineJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KlineWriter implements ItemWriter<Kline> {

    private final KlineJdbcRepository klineJdbcRepository;
    private long totalInserted = 0;
    private long lastLogTime = System.currentTimeMillis();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));

    @Override
    @SuppressWarnings("unchecked")
    public void write(final Chunk<? extends Kline> items) throws Exception {
        if (items.isEmpty()) return;

        List<Kline> klineList = (List<Kline>) items.getItems();
        klineJdbcRepository.bulkInsertIgnore(klineList);

        long now = System.currentTimeMillis();
        long elapsedMillis = now - lastLogTime;
        lastLogTime = now;

        totalInserted += klineList.size();

        double rowsPerSecond = elapsedMillis > 0
                ? (klineList.size() * 1000.0) / elapsedMillis
                : 0.0;

        String formattedStart = FORMATTER.format(Instant.ofEpochMilli(klineList.get(0).getOpenTime()));

        log.info("[KlineWriter] Start: {} | Inserted: {} rows | Total: {} | Rate: {} rows/sec",
                formattedStart, klineList.size(), totalInserted, String.format("%.2f", rowsPerSecond));
    }
}