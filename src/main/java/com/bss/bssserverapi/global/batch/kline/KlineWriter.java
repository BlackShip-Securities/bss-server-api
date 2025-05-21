package com.bss.bssserverapi.global.batch.kline;

import com.bss.bssserverapi.domain.kline.Kline;
import com.bss.bssserverapi.domain.kline.repository.KlineJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KlineWriter implements ItemWriter<Kline> {

    private final KlineJdbcRepository klineJdbcRepository;

    @Override
    @SuppressWarnings("unchecked")
    public void write(final Chunk<? extends Kline> items) throws Exception {

        if (!items.isEmpty())   klineJdbcRepository.bulkInsertIgnore((List<Kline>) items.getItems());
    }
}
