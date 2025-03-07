package com.bss.bssserverapi.domain.Auth.repository;

import com.bss.bssserverapi.domain.Auth.Auth;
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
    public Optional<String> refreshToken(final String userId) {

        return authJpaRepository.findRefreshTokenById(userId);
    }

    @Override
    public void delete(final String userId) {

        authJpaRepository.deleteById(userId);
    }
}
