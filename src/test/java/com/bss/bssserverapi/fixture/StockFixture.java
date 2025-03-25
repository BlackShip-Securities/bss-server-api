package com.bss.bssserverapi.fixture;

import com.bss.bssserverapi.domain.stock.Stock;
import org.springframework.test.util.ReflectionTestUtils;

public class StockFixture {

    public static final Stock 주식_0(){

        Stock stock = Stock.builder()
                .name("주식_0")
                .stockCode("000001")
                .marketCap(10000000L)
                .build();

        ReflectionTestUtils.setField(stock, "id", 1L);

        return stock;
    }
}
