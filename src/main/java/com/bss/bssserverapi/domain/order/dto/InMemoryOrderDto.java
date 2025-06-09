package com.bss.bssserverapi.domain.order.dto;

import com.bss.bssserverapi.domain.order.Order;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InMemoryOrderDto {

    public Long cryptoId;

    public Long orderId;

    public Long accountId;

    public BigDecimal price;

    public BigDecimal qty;

    @Builder
    public InMemoryOrderDto(final Long cryptoId, final Long orderId, final Long accountId, final BigDecimal price, final BigDecimal qty) {

        this.cryptoId = cryptoId;
        this.orderId = orderId;
        this.accountId = accountId;
        this.price = price;
        this.qty = qty;
    }

    public static InMemoryOrderDto fromEntity(final Order order) {

        return InMemoryOrderDto.builder()
                .cryptoId(order.getCrypto().getId())
                .orderId(order.getId())
                .accountId(order.getAccount().getId())
                .price(order.getPrice())
                .qty(order.getRemainingQuantity())
                .build();
    }

    public void subtractQty(final BigDecimal executeQty) {

        this.qty = this.qty.subtract(executeQty);
    }
}
