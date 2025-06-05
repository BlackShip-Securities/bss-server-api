package com.bss.bssserverapi.domain.order;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.trade.Trade;
import com.bss.bssserverapi.global.common.DateTimeField;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends DateTimeField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private SideType sideType;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal remainingQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_id")
    private Crypto crypto;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<Trade> tradeList;

    @Builder
    public Order(final SideType sideType, final OrderType orderType, final StatusType statusType, final BigDecimal price, final BigDecimal quantity, final BigDecimal remainingQuantity) {

        this.sideType = sideType;
        this.orderType = orderType;
        this.statusType = statusType;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = remainingQuantity;
        this.tradeList = new ArrayList<>();
    }

    public void setAccount(final Account account) {

        this.account = account;
    }

    public void setCrypto(final Crypto crypto) {

        this.crypto = crypto;
    }

    public void updateStatusType(final StatusType statusType) {

        this.statusType = statusType;
    }

    public void addTrade(final Trade trade) {

        this.tradeList.add(trade);
        trade.setOrder(this);
        this.remainingQuantity = this.remainingQuantity.subtract(trade.getQuantity());
    }
}
