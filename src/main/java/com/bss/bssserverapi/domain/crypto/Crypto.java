package com.bss.bssserverapi.domain.crypto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "crypto", uniqueConstraints = @UniqueConstraint(columnNames = "symbol"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crypto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 20)
    private String baseAsset;

    @Column(nullable = false, length = 20)
    private String quoteAsset;

    private Boolean isSpotTradingAllowed;
    private Boolean isMarginTradingAllowed;

    @Builder
    public Crypto(final String symbol, final String baseAsset, final String quoteAsset, final Boolean isSpotTradingAllowed, final Boolean isMarginTradingAllowed) {

        this.symbol = symbol;
        this.baseAsset = baseAsset;
        this.quoteAsset = quoteAsset;
        this.isSpotTradingAllowed = isSpotTradingAllowed;
        this.isMarginTradingAllowed = isMarginTradingAllowed;
    }
}