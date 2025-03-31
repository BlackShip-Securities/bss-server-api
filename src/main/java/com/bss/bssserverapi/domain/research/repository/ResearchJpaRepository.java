package com.bss.bssserverapi.domain.research.repository;

import com.bss.bssserverapi.domain.research.Research;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResearchJpaRepository extends JpaRepository<Research, Long> {

    @Query("SELECT r FROM Research r WHERE r.user.userName = :userName ORDER BY r.id DESC LIMIT :limit")
    List<Research> findFirstPageByUserName(@Param("userName") final String userName,
                                          @Param("limit") final Long limit);

    @Query("SELECT r FROM Research r WHERE r.user.userName = :userName AND r.id < :lastResearchId ORDER BY r.id DESC LIMIT :limit")
    List<Research> findNextPageByUserName(@Param("userName") final String userName,
                                         @Param("limit") final Long limit,
                                         @Param("lastResearchId") final Long lastResearchId);

    @Query("SELECT r FROM Research r LEFT JOIN FETCH r.user WHERE r.stock.id = :stockId ORDER BY r.id DESC LIMIT :limit")
    List<Research> findFirstPageByStockId(@Param("stockId") final Long stockId,
                                          @Param("limit") final Long limit);

    @Query("SELECT r FROM Research r LEFT JOIN FETCH r.user WHERE r.stock.id = :stockId AND r.id < :lastResearchId ORDER BY r.id DESC LIMIT :limit")
    List<Research> findNextPageByStockId(@Param("stockId") final Long stockId,
                                         @Param("limit") final Long limit,
                                         @Param("lastResearchId") final Long lastResearchId);
}
