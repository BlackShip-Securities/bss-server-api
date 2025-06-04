package com.bss.bssserverapi.domain.holding;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "holding",
        uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "crypto_id"})
)
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

    public Holding(final Account account, final Crypto crypto) {

        this.account = account;
        this.crypto = crypto;
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

    public void applyLongTrade(final BigDecimal tradeQty, final BigDecimal cost) {

        this.quantity = this.quantity.add(tradeQty);
        this.totalPrice = this.totalPrice.add(cost);

        if (this.quantity.compareTo(BigDecimal.ZERO) == 0) {
            this.avgBuyPrice = BigDecimal.ZERO;
        } else {
            this.avgBuyPrice = this.totalPrice.divide(this.quantity, 7, RoundingMode.HALF_UP);
        }
    }

    public void applyShortTrade(final BigDecimal tradeQty, final BigDecimal cost) {

        this.quantity = this.quantity.subtract(tradeQty);
        this.totalPrice = this.totalPrice.subtract(cost);

        if (this.quantity.compareTo(BigDecimal.ZERO) == 0) {
            this.avgBuyPrice = BigDecimal.ZERO;
        } else {
            this.avgBuyPrice = this.totalPrice.divide(this.quantity, 7, RoundingMode.HALF_UP);
        }
    }
}
