package com.bss.bssserverapi.domain.stock.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetStockResDto {

    private String stockCode;
    private String name;
    private Long marketCap;

    @Builder
    public GetStockResDto(String stockCode, String name, Long marketCap) {

        this.stockCode = stockCode;
        this.name = name;
        this.marketCap = marketCap;
    }
}
