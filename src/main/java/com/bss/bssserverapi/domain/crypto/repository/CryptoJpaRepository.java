package com.bss.bssserverapi.domain.crypto.repository;

import com.bss.bssserverapi.domain.crypto.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoJpaRepository extends JpaRepository<Crypto, Long> {

    Boolean existsBySymbol(final String symbol);
}
