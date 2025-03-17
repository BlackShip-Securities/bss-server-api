package com.bss.bssserverapi.domain.research.repository;

import com.bss.bssserverapi.domain.research.ResearchTag;
import com.bss.bssserverapi.domain.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResearchTagRepository extends JpaRepository<ResearchTag, Long> {

    List<ResearchTag> findResearchTagsByResearchId(final Long researchId);
}
