package ru.alta.svd.client.core.listeners.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.alta.svd.client.core.domain.entity.Account;

public class PushFileRouteStart extends ApplicationEvent {
    @Getter
    private Account account;

    public PushFileRouteStart(Object source, Account account) {
        super(source);
        this.account = account;
    }
}
