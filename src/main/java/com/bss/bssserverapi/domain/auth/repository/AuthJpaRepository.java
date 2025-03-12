package com.bss.bssserverapi.domain.auth.repository;

import com.bss.bssserverapi.domain.auth.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthJpaRepository extends JpaRepository<Auth, String> {

    Optional<Auth> findAuthByUserName(final String userName);
    void deleteByUserName(final String userName);
}
