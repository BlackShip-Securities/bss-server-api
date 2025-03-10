package com.bss.bssserverapi.domain.user.repository;

import com.bss.bssserverapi.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(final String userId);
    boolean existsByUserId(final String userId);
}
