package com.bss.bssserverapi.domain.research.repository;

import com.bss.bssserverapi.domain.research.Research;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchJpaRepository extends JpaRepository<Research, Long> {

    Slice<Research> findAllByUserId(Long userId, Pageable pageable);
    Slice<Research> findAllByStockId(Long stockId, Pageable pageable);
}
