package com.bss.bssserverapi.fixture;

import com.bss.bssserverapi.domain.stock.Stock;

public class StockFixture {

    public static final Stock 주식_0(){

        return Stock.builder()
                .name("주식_0")
                .stockCode("000001")
                .marketCap(10000000L)
                .build();
    }
}
