package com.bss.bssserverapi.domain.kline.repository;

import com.bss.bssserverapi.domain.kline.Kline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KlineJpaRepository extends JpaRepository<Kline, Long> {
}
