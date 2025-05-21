package com.bss.bssserverapi.domain.kline.repository;

import com.bss.bssserverapi.domain.kline.Kline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KlineJpaRepository extends JpaRepository<Kline, Long> {

    @Query("""
        SELECT MAX(k.openTime) 
        FROM Kline k 
        WHERE k.symbol = :symbol 
          AND k.interval = :interval 
          AND k.openTime >= :afterOpenTime
    """)
    Optional<Long> findLatestOpenTimeWithinRange(
            @Param("symbol") String symbol,
            @Param("interval") String interval,
            @Param("afterOpenTime") Long afterOpenTime
    );
}