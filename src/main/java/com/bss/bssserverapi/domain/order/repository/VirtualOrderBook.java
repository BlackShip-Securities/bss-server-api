package com.bss.bssserverapi.domain.order.repository;

import com.bss.bssserverapi.domain.order.dto.InMemoryOrderDto;
import lombok.Getter;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;

@Getter
public class VirtualOrderBook {

    private final NavigableSet<InMemoryOrderDto> asks = new TreeSet<>(
            Comparator.comparing(InMemoryOrderDto::getPrice)
                    .thenComparing(InMemoryOrderDto::getOrderId)
    );

    private final NavigableSet<InMemoryOrderDto> bids = new TreeSet<>(
            Comparator.comparing(InMemoryOrderDto::getPrice).reversed()
                    .thenComparing(InMemoryOrderDto::getOrderId)
    );
}