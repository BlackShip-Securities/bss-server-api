package com.bss.bssserverapi.domain.stock.repository;

import com.bss.bssserverapi.domain.stock.Stock;

import java.util.Optional;

public interface StockRepository {

    Optional<Stock> findStockByName(final String name);
    Optional<Stock> findStockByStockCode(final String stockCode);
}
