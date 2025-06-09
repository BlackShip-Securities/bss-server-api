package com.bss.bssserverapi.domain.closing_profit_loss;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.holding.Holding;
import com.bss.bssserverapi.domain.trade.Trade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity(name = "closing_profit_loss")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ClosingProfitLoss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal entryPrice;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal matchedPrice;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal matchedProfit;

    @Column(precision = 19, scale = 1, nullable = false)
    private BigDecimal matchedProfitRate;

    @CreatedDate
    private LocalDateTime matchedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private Trade trade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_id")
    private Crypto crypto;

    public void calculateFromTradeAndHolding(final Trade trade, final Holding holding) {

        this.entryPrice = holding.getAvgBuyPrice();
        this.matchedPrice = trade.getPrice();
        final BigDecimal quantity = trade.getQuantity();

        this.matchedProfit = matchedPrice.subtract(entryPrice).multiply(quantity);

        if (entryPrice.compareTo(BigDecimal.ZERO) == 0) {
            this.matchedProfitRate = BigDecimal.ZERO;
        } else {
            this.matchedProfitRate = matchedPrice.subtract(entryPrice).divide(entryPrice, 1, RoundingMode.HALF_UP);
        }
    }

    public void setAccount(final Account account) {

        this.account = account;
    }

    public void setTrade(final Trade trade) {

        this.trade = trade;
    }

    public void setCrypto(final Crypto crypto) {

        this.crypto = crypto;
    }


}
