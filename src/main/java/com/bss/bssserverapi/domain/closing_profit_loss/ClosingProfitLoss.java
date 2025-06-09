package com.bss.bssserverapi.domain.closing_profit_loss;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.trade.Trade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "closing_profit_loss")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClosingProfitLoss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal entryPrice;

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

    @Builder
    public ClosingProfitLoss(final BigDecimal entryPrice, final BigDecimal matchedProfit, final BigDecimal matchedProfitRate) {

        this.entryPrice = entryPrice;
        this.matchedProfit = matchedProfit;
        this.matchedProfitRate = matchedProfitRate;
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
