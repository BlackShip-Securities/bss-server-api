package com.bss.bssserverapi.domain.trade;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.order.Order;
import com.bss.bssserverapi.domain.order.SideType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private SideType sideType;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal amount;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal fee;

    @CreatedDate
    private LocalDateTime matchedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_id")
    private Crypto crypto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Builder
    public Trade(final SideType sideType, final BigDecimal price, final BigDecimal quantity, final BigDecimal amount, final BigDecimal fee) {

        this.sideType = sideType;
        this.price = price;
        this.quantity = quantity;
        this.amount = amount;
        this.fee = fee;
    }

    public void setAccount(final Account account) {

        this.account = account;
    }

    public void setCrypto(final Crypto crypto) {

        this.crypto = crypto;
    }

    public void setOrder(final Order order) {

        this.order = order;
    }
}
