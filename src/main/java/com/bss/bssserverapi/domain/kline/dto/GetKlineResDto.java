package com.bss.bssserverapi.domain.kline.dto;

import com.bss.bssserverapi.domain.kline.Kline;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class GetKlineResDto {

    private Long openTime;
    private Long closeTime;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal baseVolume;
    private Integer tradeCount;

    public static GetKlineResDto toDto(final Kline kline) {

        return new GetKlineResDto(
                kline.getOpenTime(),
                kline.getCloseTime(),
                kline.getOpenPrice(),
                kline.getClosePrice(),
                kline.getHighPrice(),
                kline.getLowPrice(),
                kline.getBaseVolume(),
                kline.getTradeCount()
        );
    }
}
