package com.bss.bssserverapi.domain.crypto.repository;

import com.bss.bssserverapi.domain.crypto.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CryptoJpaRepository extends JpaRepository<Crypto, Long> {

    Boolean existsBySymbol(final String symbol);

    @Query("select c.symbol from Crypto c")
    List<String> findAllSymbols();
}
