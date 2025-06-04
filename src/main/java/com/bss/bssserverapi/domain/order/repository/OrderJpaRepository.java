package com.bss.bssserverapi.domain.order.repository;

import com.bss.bssserverapi.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
