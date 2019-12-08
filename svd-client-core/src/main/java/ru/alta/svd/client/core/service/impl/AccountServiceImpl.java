package ru.alta.svd.client.core.service.impl;

import lombok.AllArgsConstructor;
import org.apache.camel.util.FileUtil;
import org.ini4j.Ini;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alta.svd.client.core.dao.repository.AccountRepository;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.listeners.events.AccountRefreshEvent;
import ru.alta.svd.client.core.service.api.AccountService;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.client.core.validator.AccountErrors;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static ru.alta.svd.client.core.validator.OperationLogErrors.*;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OperationLogService operationLogService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RoutesServiceImpl routesService;

    public void delete(Account account) throws Exception {
        accountRepository.delete(account);
        routesService.stopOldRoutes(account);
        applicationEventPublisher.publishEvent(new AccountRefreshEvent(this, null));
    }

    public List<Account> findAccounts() {
        return accountRepository.findAll();
    }

    public void save(Account account) throws Exception {
        if (account.getId() != null && routesService.routesExists(account)) {
            Account dbAccount = accountRepository.findById(account.getId());
            if (dbAccount != null) {
                boolean requestIntervalOk = dbAccount.getRequestInterval() == null || dbAccount.getRequestInterval().equals(account.getRequestInterval());
                boolean loginOk = dbAccount.getLogin() != null && dbAccount.getLogin().equals(account.getLogin());
                boolean serverIdOk = dbAccount.getServerId() != null && dbAccount.getServerId().equals(account.getServerId());
                if (loginOk && requestIntervalOk && serverIdOk) {
                    accountRepository.save(account);
                    if (validateForRoute(account)) {
                        if (dbAccount.getSendDir() == null || !dbAccount.getSendDir().equals(account.getSendDir()))
                            routesService.restartPushFilesRoute(account);

                        routesService.refreshAccountRoutes(account);
                    } else {
                        routesService.stopOldRoutes(account);
                    }
                } else {
                    routesService.stopOldRoutes(dbAccount);
                    accountRepository.save(account);
                    applicationEventPublisher.publishEvent(new AccountRefreshEvent(this, account));
                }
            }
        } else {
            if (account.getId() != null) {
                Account dbAccount = accountRepository.findById(account.getId());
                routesService.stopOldRoutes(dbAccount);
            }
            accountRepository.save(account);
            applicationEventPublisher.publishEvent(new AccountRefreshEvent(this, account));
        }
    }

    @Override
    public Optional<Account> findOne(Integer id) {
        return accountRepository.findOne(id);
    }

    @Override
    public void saveFromFile(File file) throws Exception {
        ZipFile zipFile = new ZipFile(file);
        ZipEntry zipEntry = zipFile.getEntry("config.ini");
        Ini ini = new Ini(zipFile.getInputStream(zipEntry));
        Account account = Account.builder()
                .login(ini.get("ED", "HTTPLogin", String.class))
                .password(ini.get("ED", "HttpPassword", String.class))
                .requestInterval(10000)
                .build();
        save(account);
        FileUtil.deleteFile(file);
    }

    @Override
    public int validate(Account account) {
        return accountRepository.validate(account);
    }

    @Override
    public int checkTransportError(Account account) {
        OperationLog operationLog = null;
        try {
            operationLog = operationLogService.findLastErrorByLogin(account.getLogin());
        } catch (Exception e) {
            return RECEIVE_ERROR;
        }
        if (operationLog == null) return OK;
        switch (operationLog.getType()) {
            case RECEIVE:
                return RECEIVE_ERROR;
            case SEND:
                return SEND_ERROR;
            case DELETE:
                return DELETE_ERROR;
            default:
                return OK;
        }
    }

    @Override
    public Integer checkDuplicates(Account account) {
        List<Account> accounts = accountRepository.findAll();
        long countSend = accounts.stream().filter(acc -> acc.getSendDir() != null && account.getSendDir() != null && acc.getSendDir().equalsIgnoreCase(account.getSendDir())).count();
        if (countSend > 1) return AccountErrors.DUPLICATE_SEND;
        if (accounts.stream().filter(acc -> acc.getLogin() != null && account.getLogin() != null && acc.getLogin().equals(account.getLogin())).count() > 1)
            return AccountErrors.DUPLICATE_LOGIN;
        return AccountErrors.OK;
    }

    public boolean validateForRoute(Account account) {
        return accountRepository.validateForRoute(account);
    }
}
