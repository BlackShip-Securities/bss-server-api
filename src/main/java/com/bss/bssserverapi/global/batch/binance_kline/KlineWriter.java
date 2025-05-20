package com.bss.bssserverapi.global.batch.binance_kline;

import com.bss.bssserverapi.domain.kline.Kline;
import com.bss.bssserverapi.domain.kline.repository.KlineJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KlineWriter implements ItemWriter<Kline> {

    private final KlineJpaRepository klineJpaRepository;

    @Override
    public void write(final Chunk<? extends Kline> items) throws Exception {

        if (!items.isEmpty())   klineJpaRepository.saveAll(items);
    }
}
