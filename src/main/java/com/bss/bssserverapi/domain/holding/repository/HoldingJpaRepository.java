package com.bss.bssserverapi.domain.holding.repository;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.holding.Holding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HoldingJpaRepository extends JpaRepository<Holding, Long> {

    Optional<Holding> findByAccountAndCrypto(final Account account, final Crypto crypto);
}
