package com.bss.bssserverapi.domain.Auth.repository;

import com.bss.bssserverapi.domain.Auth.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthJpaRepository extends JpaRepository<Auth, String> {

    Optional<String> findRefreshTokenById(final String userId);
}
