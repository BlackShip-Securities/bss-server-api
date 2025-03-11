package com.bss.bssserverapi.domain.auth.repository;

import com.bss.bssserverapi.domain.auth.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository{

    private final AuthJpaRepository authJpaRepository;

    @Override
    public void save(final String userId, final String refreshToken, final Date expiredDate) {

        authJpaRepository.save(Auth.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiredTime(expiredDate)
                .build());
    }

    @Override
    public Optional<Auth> findAuthByUserId(final String userId) {

        return authJpaRepository.findAuthByUserId(userId);
    }

    @Override
    public void deleteByUserId(final String userId) {

        authJpaRepository.deleteById(userId);
    }
}
