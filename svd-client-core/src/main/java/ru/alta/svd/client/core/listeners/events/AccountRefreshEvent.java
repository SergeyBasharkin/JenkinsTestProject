package ru.alta.svd.client.core.listeners.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.alta.svd.client.core.domain.entity.Account;

public class AccountRefreshEvent extends ApplicationEvent {
    @Getter
    private Account account;

    public AccountRefreshEvent(Object source, Account account) {
        super(source);
        this.account = account;
    }
}
