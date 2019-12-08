package ru.alta.svd.client.core.listeners.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.alta.svd.client.core.domain.entity.Configuration;

public class ConfigurationEvent extends ApplicationEvent {

    @Getter
    private Configuration configuration;

    public ConfigurationEvent(Object source, Configuration configuration) {
        super(source);
        this.configuration = configuration;
    }
}

