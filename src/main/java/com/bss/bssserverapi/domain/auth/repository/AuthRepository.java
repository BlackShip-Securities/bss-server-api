package com.bss.bssserverapi.domain.auth.repository;

import com.bss.bssserverapi.domain.auth.Auth;

import java.util.Date;
import java.util.Optional;

public interface AuthRepository {

    void save(final String userId, final String token, final Date expiredDate);

    Optional<Auth> findAuthByUserId(final String userId);

    void deleteByUserId(final String userId);
}
