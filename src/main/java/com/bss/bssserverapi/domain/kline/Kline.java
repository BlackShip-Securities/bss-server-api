package com.bss.bssserverapi.domain.kline;

import com.bss.bssserverapi.global.websocket.binance.dto.BinanceKlineMessage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"symbol", "interval_kline", "openTime"})
})
public class Kline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String symbol;

    @Column(name = "interval_kline", length = 5, nullable = false)
    private String interval;

    @Column(nullable = false)
    private Long openTime;

    @Column(nullable = false)
    private Long closeTime;

    @Column(precision = 30, scale = 12)
    private BigDecimal openPrice;

    @Column(precision = 30, scale = 12)
    private BigDecimal closePrice;

    @Column(precision = 30, scale = 12)
    private BigDecimal highPrice;

    @Column(precision = 30, scale = 12)
    private BigDecimal lowPrice;

    @Column(precision = 30, scale = 12)
    private BigDecimal baseVolume;

    private Integer tradeCount;

    @Column(precision = 30, scale = 12)
    private BigDecimal quoteVolume;

    @Column(precision = 30, scale = 12)
    private BigDecimal takerBuyBaseVolume;

    @Column(precision = 30, scale = 12)
    private BigDecimal takerBuyQuoteVolume;

    public Kline(final String symbol, final String interval, final Long openTime, final Long closeTime, final BigDecimal openPrice, final BigDecimal closePrice, final BigDecimal highPrice, final BigDecimal lowPrice, final BigDecimal baseVolume, final Integer tradeCount, final BigDecimal quoteVolume, final BigDecimal takerBuyBaseVolume, final BigDecimal takerBuyQuoteVolume) {

        this.symbol = symbol;
        this.interval = interval;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.baseVolume = baseVolume;
        this.tradeCount = tradeCount;
        this.quoteVolume = quoteVolume;
        this.takerBuyBaseVolume = takerBuyBaseVolume;
        this.takerBuyQuoteVolume = takerBuyQuoteVolume;
    }

    public static Kline fromBinanceKlineMessage(final BinanceKlineMessage message) {

        BinanceKlineMessage.KlineData data = message.getKline();

        return new Kline(
                data.getSymbol(),
                data.getInterval(),
                data.getStartTime(),
                data.getCloseTime(),
                new BigDecimal(data.getOpenPrice()),
                new BigDecimal(data.getClosePrice()),
                new BigDecimal(data.getHighPrice()),
                new BigDecimal(data.getLowPrice()),
                new BigDecimal(data.getBaseVolume()),
                data.getTradeCount().intValue(),
                new BigDecimal(data.getQuoteVolume()),
                new BigDecimal(data.getTakerBuyBaseVolume()),
                new BigDecimal(data.getTakerBuyQuoteVolume())
        );
    }
}
