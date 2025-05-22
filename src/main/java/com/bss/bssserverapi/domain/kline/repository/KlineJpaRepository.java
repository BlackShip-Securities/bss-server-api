package com.bss.bssserverapi.domain.kline.repository;

import com.bss.bssserverapi.domain.kline.Kline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KlineJpaRepository extends JpaRepository<Kline, Long> {

    @Query("""
        SELECT MAX(k.openTime)
        FROM Kline k
        WHERE k.symbol = :symbol
          AND k.interval = :interval
          AND k.openTime <= :endTime
    """)
    Optional<Long> findLatestOpenTimeWithinRange(
            @Param("symbol") final String symbol,
            @Param("interval") final String interval,
            @Param("endTime") final Long endTime
    );

    @Query("SELECT k FROM Kline k WHERE k.symbol = :symbol AND k.interval = :interval ORDER BY k.openTime DESC LIMIT :limit")
    List<Kline> findFirstPageBySymbolAndInterval(@Param("symbol") final String symbol,
                                                 @Param("interval") final String interval,
                                                 @Param("limit") final Long limit);

    @Query("SELECT k FROM Kline k WHERE k.symbol = :symbol AND k.interval = :interval AND k.openTime < :lastOpenTime ORDER BY k.openTime DESC LIMIT :limit")
    List<Kline> findNextPageBySymbolAndInterval(@Param("symbol") final String symbol,
                                                @Param("interval") final String interval,
                                                @Param("limit") final Long limit,
                                                @Param("lastOpenTime") final Long lastOpenTime);
}