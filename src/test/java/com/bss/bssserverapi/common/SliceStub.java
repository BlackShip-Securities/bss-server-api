package com.bss.bssserverapi.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class SliceStub<T> extends SliceImpl<T> {
    public SliceStub(List<T> content, boolean hasNext) {
        super(content, Pageable.unpaged(), hasNext);
    }
}