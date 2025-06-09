package com.bss.bssserverapi.domain.trade.repository;

import com.bss.bssserverapi.domain.trade.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeJpaRepository extends JpaRepository<Trade, Long> {
}
