package com.bss.bssserverapi.global.batch.binance_kline;

import com.bss.bssserverapi.domain.kline.Kline;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KlineProcessor implements ItemProcessor<Kline, Kline> {


    @Override
    public Kline process(final Kline item) throws Exception {

        return item;
    }
}
