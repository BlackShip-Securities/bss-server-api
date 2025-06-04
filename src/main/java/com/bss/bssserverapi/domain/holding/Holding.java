package com.bss.bssserverapi.domain.holding;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 7, nullable = false)
    private BigDecimal totalPrice;

    @Column(precision = 19, scale = 7, nullable = false)
    private BigDecimal avgBuyPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_id")
    private Crypto crypto;

    public Holding() {

        this.quantity = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
        this.avgBuyPrice = BigDecimal.ZERO;
    }

    public void setAccount(final Account account) {

        this.account = account;
    }

    public void setCrypto(final Crypto crypto) {

        this.crypto = crypto;
    }

    public void applyBuyTrade(final BigDecimal tradeQty, final BigDecimal cost) {

        this.quantity = this.quantity.add(tradeQty);
        this.totalPrice = this.totalPrice.add(cost);
        this.avgBuyPrice = this.totalPrice.divide(this.quantity, 7, RoundingMode.HALF_UP);
    }
}
