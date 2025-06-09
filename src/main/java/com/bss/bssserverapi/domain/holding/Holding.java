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

    public Holding(final Account account, final Crypto crypto) {

        this.account = account;
        this.crypto = crypto;
    }

    public void setAccount(final Account account) {

        this.account = account;
    }

    public void setCrypto(final Crypto crypto) {

        this.crypto = crypto;
    }

    public void applyLongTrade(final Trade trade) {

        final BigDecimal tradeQty = trade.getQuantity();
        final BigDecimal tradePrice = trade.getPrice();

        final BigDecimal tradeTotal = tradeQty.multiply(tradePrice);

        this.totalPrice = this.totalPrice.add(tradeTotal);
        this.quantity = this.quantity.add(tradeQty);

        if (this.quantity.compareTo(BigDecimal.ZERO) > 0) {
            this.avgBuyPrice = this.totalPrice.divide(this.quantity, 7, RoundingMode.HALF_UP);
        } else {
            this.avgBuyPrice = BigDecimal.ZERO;
        }
    }

    public void applyShortTrade(final Trade trade) {

        final BigDecimal tradeQty = trade.getQuantity();

        // TODO: 밖으로 뺄지 여기 나둘지
//        if (this.quantity.compareTo(tradeQty) < 0) {
//            throw new IllegalStateException("보유 수량보다 많은 매도를 시도했습니다.");
//        }

        this.quantity = this.quantity.subtract(tradeQty);
        this.totalPrice = this.totalPrice.subtract(this.avgBuyPrice.multiply(tradeQty));

        if (this.quantity.compareTo(BigDecimal.ZERO) <= 0) {
            this.quantity = BigDecimal.ZERO;
            this.totalPrice = BigDecimal.ZERO;
            this.avgBuyPrice = BigDecimal.ZERO;
        }
    }
}
