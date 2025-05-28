package com.bss.bssserverapi.domain.trade;

import com.bss.bssserverapi.domain.account.Account;
import com.bss.bssserverapi.domain.crypto.Crypto;
import com.bss.bssserverapi.domain.order.Order;
import com.bss.bssserverapi.domain.order.SideType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SideType sideType;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal Quantity;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal price;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal fee;

    private LocalDateTime matchedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Crypto crypto;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

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
