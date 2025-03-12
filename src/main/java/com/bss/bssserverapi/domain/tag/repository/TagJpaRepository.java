package com.bss.bssserverapi.domain.tag.repository;

import com.bss.bssserverapi.domain.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

    boolean existsTagByName(final String name);
}
