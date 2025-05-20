package com.bss.bssserverapi.global.batch.binance_kline;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class KlineJobTriggerEvent extends ApplicationEvent {

    public KlineJobTriggerEvent(final Object source) {

        super(source);
    }
}
