package com.bss.bssserverapi.domain.stock.repository;

import com.bss.bssserverapi.domain.stock.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository{

    private final StockJpaRepository stockJpaRepository;

    @Override
    public Optional<Stock> findStockById(Long id) {

        return stockJpaRepository.findById(id);
    }

    @Override
    public Optional<Stock> findStockByName(String name) {

        return stockJpaRepository.findStockByName(name);
    }

    @Override
    public Optional<Stock> findStockByStockCode(String stockCode) {

        return stockJpaRepository.findStockByStockCode(stockCode);
    }
}
