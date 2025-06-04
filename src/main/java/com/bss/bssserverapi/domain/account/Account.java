package com.bss.bssserverapi.domain.account;

import com.bss.bssserverapi.domain.holding.Holding;
import com.bss.bssserverapi.domain.order.Order;
import com.bss.bssserverapi.domain.trade.Trade;
import com.bss.bssserverapi.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 7, nullable = false)
    private BigDecimal balance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "account")
    private List<Order> orderList;

    @OneToMany(mappedBy = "account")
    private List<Trade> tradeList;

    @OneToMany(mappedBy = "account")
    private List<Holding> holdingList;

    public Account(final BigDecimal balance){

        this.balance = balance;
    }

    public void addOrder(final Order order){

        this.orderList.add(order);
        order.setAccount(this);
    }

    public void addTrade(final Trade trade){

        this.tradeList.add(trade);
        trade.setAccount(this);

        switch (trade.getSideType()) {
            case LONG:
                this.balance = this.balance.subtract(trade.getCost());
                break;
            case SHORT:
                this.balance = this.balance.add(trade.getCost());
                break;
        }
    }

    public void addHolding(final Holding holding){

        this.holdingList.add(holding);
        holding.setAccount(this);
    }

    public void setUser(final User user){

        this.user = user;
    }
}
