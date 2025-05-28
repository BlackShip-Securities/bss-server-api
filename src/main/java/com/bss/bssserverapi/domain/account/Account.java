package com.bss.bssserverapi.domain.account;

import com.bss.bssserverapi.domain.holding.Holding;
import com.bss.bssserverapi.domain.order.Order;
import com.bss.bssserverapi.domain.trade.Trade;
import com.bss.bssserverapi.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 5, nullable = false)
    private BigDecimal balance;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "account")
    private List<Order> orderList;

    @OneToMany(mappedBy = "account")
    private List<Trade> tradeList;

    @OneToMany(mappedBy = "account")
    private List<Holding> holdingList;

    public void addOrder(final Order order){

        this.orderList.add(order);
        order.setAccount(this);
    }

    public void addTrade(final Trade trade){

        this.tradeList.add(trade);
        trade.setAccount(this);
    }

    public void addHolding(final Holding holding){

        this.holdingList.add(holding);
        holding.setAccount(this);
    }
}
