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
    public void save(final String userName, final String refreshToken, final Date expiredDate) {

        authJpaRepository.save(Auth.builder()
                .userName(userName)
                .refreshToken(refreshToken)
                .expiredTime(expiredDate)
                .build());
    }

    @Override
    public Optional<Auth> findAuthByUserName(final String userName) {

        return authJpaRepository.findAuthByUserName(userName);
    }

    @Override
    public void deleteByUserName(final String userName) {

        authJpaRepository.deleteByUserName(userName);
    }
}
