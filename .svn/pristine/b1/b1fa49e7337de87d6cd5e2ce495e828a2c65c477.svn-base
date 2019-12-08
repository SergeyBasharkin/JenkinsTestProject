package ru.alta.svd.client.core.listeners;

import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.listeners.events.*;
import ru.alta.svd.client.core.service.api.AccountService;
import ru.alta.svd.client.core.service.api.ConfigurationService;
import ru.alta.svd.client.core.service.impl.ConfigurationServiceImpl;
import ru.alta.svd.client.core.service.impl.RoutesServiceImpl;
import ru.alta.svd.client.core.validator.ConfigurationErrors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SvdListener {

    private final ConfigurationService configurationService;
    private final AccountService accountService;
    private final RoutesServiceImpl routesService;

    @EventListener
    public void configRegistered(ConfigurationEvent configurationEvent) {
        List<Account> accounts = accountService.findAccounts();
        if (!accounts.isEmpty() &&
                configurationEvent.getConfiguration() != null &&
                Configuration.validate(configurationEvent.getConfiguration()) == ConfigurationErrors.OK)
            routesService.startRoutes(accounts.stream().filter(accountService::validateForRoute).collect(Collectors.toList()), configurationEvent.getConfiguration());
    }

    @EventListener
    public void refreshAccount(AccountRefreshEvent event) {
        Configuration configuration = configurationService.loadConfig();
        List<Account> accounts = accountService.findAccounts();
        if (Configuration.configurationIsSet(configuration) &&
                event.getAccount() != null &&
                Configuration.validate(configuration) == ConfigurationErrors.OK)
            routesService.startRoutes(accounts.stream().filter(accountService::validateForRoute).collect(Collectors.toList()), configuration);
    }

    @EventListener
    public void pushFileRouteStart(PushFileRouteStart event) throws Exception {
        Configuration configuration = configurationService.loadConfig();
        if (Configuration.configurationIsSet(configuration) &&
                event.getAccount() != null && accountService.validateForRoute(event.getAccount()) &&
                Configuration.validate(configuration) == ConfigurationErrors.OK) {
            routesService.startPushFilesRoute(event.getAccount(), configuration);
        }
    }

    @EventListener
    public void registerRoutes(ContextRefreshedEvent contextRefreshedEvent) {
        Configuration configuration = configurationService.loadConfig();
        List<Account> accounts = accountService.findAccounts();
        if (Configuration.configurationIsSet(configuration) &&
                !accounts.isEmpty() &&
                Configuration.validate(configuration) == ConfigurationErrors.OK)
            routesService.startRoutes(accounts.stream().filter(accountService::validateForRoute).collect(Collectors.toList()), configuration);
    }
}
