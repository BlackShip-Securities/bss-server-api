package com.bss.bssserverapi.domain.holding;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal avgBuyPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Crypto crypto;

    public void setAccount(final Account account) {

        this.account = account;
    }

    public void setCrypto(final Crypto crypto) {

        this.crypto = crypto;
    }
}
