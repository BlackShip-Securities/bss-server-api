package com.bss.bssserverapi.domain.research.repository;

import com.bss.bssserverapi.domain.research.Research;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchJpaRepository extends JpaRepository<Research, Long> {
}
