package com.bss.bssserverapi.domain.holding;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.order.SideType;
import com.bss.bssserverapi.domain.trade.Trade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Entity
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

    public void addTrade(final Trade trade) {

        switch (trade.getSideType()) {
            case LONG:
                this.quantity = this.quantity.add(trade.getQuantity());
                break;
            case SHORT:
                this.quantity = this.quantity.subtract(trade.getQuantity());
                break;
        }

        if (this.quantity.compareTo(BigDecimal.ZERO) == 0) {
            this.avgBuyPrice = BigDecimal.ZERO;
        } else {
            this.avgBuyPrice = this.totalPrice.divide(this.quantity, 7, RoundingMode.HALF_UP);
        }
    }
}
