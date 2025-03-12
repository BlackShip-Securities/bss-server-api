package com.bss.bssserverapi.domain.auth.repository;

import com.bss.bssserverapi.domain.auth.Auth;

import java.util.Date;
import java.util.Optional;

public interface AuthRepository {

    void save(final String userName, final String token, final Date expiredDate);

    Optional<Auth> findAuthByUserName(final String userName);

    void deleteByUserName(final String userName);
}
