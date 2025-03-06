package com.bss.bssserverapi.domain.Auth.repository;

import java.util.Date;
import java.util.Optional;

public interface AuthRepository {

    void save(final String userId, final String token, final Date expiredDate);

    Optional<String> refreshToken(final String userId);

    void delete(final String userId);
}
