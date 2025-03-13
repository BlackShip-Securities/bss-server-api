package com.bss.bssserverapi.domain.tag.repository;

import com.bss.bssserverapi.domain.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findTagByName(final String name);
}
