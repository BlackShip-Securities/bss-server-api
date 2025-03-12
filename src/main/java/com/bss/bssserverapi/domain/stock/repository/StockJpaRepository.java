package com.bss.bssserverapi.domain.stock.repository;

import com.bss.bssserverapi.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findStockByName(final String name);
    Optional<Stock> findStockByStockCode(final String code);
}
