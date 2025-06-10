package com.bss.bssserverapi.domain.order.repository;

import com.bss.bssserverapi.domain.order.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM orders o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}
