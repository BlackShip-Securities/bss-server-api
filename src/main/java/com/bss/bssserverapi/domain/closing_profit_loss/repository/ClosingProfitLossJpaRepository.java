package com.bss.bssserverapi.domain.closing_profit_loss.repository;

import com.bss.bssserverapi.domain.closing_profit_loss.ClosingProfitLoss;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClosingProfitLossJpaRepository extends JpaRepository<ClosingProfitLoss, Long> {
}
