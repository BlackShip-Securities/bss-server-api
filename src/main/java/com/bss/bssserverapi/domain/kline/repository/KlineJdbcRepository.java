package com.bss.bssserverapi.domain.kline.repository;

import com.bss.bssserverapi.domain.kline.Kline;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class KlineJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsertIgnore(final List<Kline> klineList) {

        String sql = "INSERT IGNORE INTO kline (symbol, interval_kline, open_time, close_time, open_price, close_price, high_price, low_price, base_volume, trade_count, quote_volume, taker_buy_base_volume, taker_buy_quote_volume) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, klineList, 1000, (ps, kline) -> {
            ps.setString(1, kline.getSymbol());
            ps.setString(2, kline.getInterval());
            ps.setLong(3, kline.getOpenTime());
            ps.setLong(4, kline.getCloseTime());
            ps.setBigDecimal(5, kline.getOpenPrice());
            ps.setBigDecimal(6, kline.getClosePrice());
            ps.setBigDecimal(7, kline.getHighPrice());
            ps.setBigDecimal(8, kline.getLowPrice());
            ps.setBigDecimal(9, kline.getBaseVolume());
            ps.setInt(10, kline.getTradeCount());
            ps.setBigDecimal(11, kline.getQuoteVolume());
            ps.setBigDecimal(12, kline.getTakerBuyBaseVolume());
            ps.setBigDecimal(13, kline.getTakerBuyQuoteVolume());
        });
    }
}