package com.bss.bssserverapi.domain.tag.service;

import com.bss.bssserverapi.domain.tag.repository.TagJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagJpaRepository tagJpaRepository;

    public boolean existTagByName(final String name) {

        return tagJpaRepository.existsTagByName(name);
    }
}
