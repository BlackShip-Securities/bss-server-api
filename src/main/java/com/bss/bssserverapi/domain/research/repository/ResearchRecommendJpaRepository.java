package com.bss.bssserverapi.domain.research.repository;

import com.bss.bssserverapi.domain.research.ResearchRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResearchRecommendJpaRepository extends JpaRepository<ResearchRecommend, Long> {

    Optional<ResearchRecommend> findByUserIdAndResearchId(final Long userId, final Long researchId);
}
