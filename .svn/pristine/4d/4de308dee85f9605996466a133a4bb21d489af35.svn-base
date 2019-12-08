package ru.alta.svd.client.core.dao.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.dao.repository.AccountRepository;
import ru.alta.svd.client.core.dao.repository.ConfigurationRepository;
import ru.alta.svd.client.core.domain.entity.types.ServerMode;
import ru.alta.svd.client.core.validator.AccountErrors;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class FileAccountRepository implements AccountRepository {

    private final ConfigurationRepository configurationRepository;

    public FileAccountRepository(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public Account findById(Integer id) {
        Configuration configuration = configurationRepository.get();
        return configuration.getAccounts().stream().filter(account -> account.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(Account account) {
        Configuration configuration = configurationRepository.get();
        configuration.getAccounts().removeIf(acc -> acc.getId().equals(account.getId()));
        configurationRepository.save(configuration);
    }

    @Override
    public List<Account> findAll() {
        Configuration configuration = configurationRepository.get();
        return configuration == null || configuration.getAccounts() == null ? new ArrayList<>() : configuration.getAccounts();
    }

    @Override
    public void save(Account account) {
        Configuration configuration = configurationRepository.get();
        if (configuration == null) configuration = Configuration.builder().build();
        initId(account, configuration);
        configurationRepository.save(configuration);
    }

    @Override
    public Optional<Account> findOne(Integer id) {
        return Optional.ofNullable(findById(id));
    }

    @Override
    public void save(List<Account> testAccounts) {
        Configuration configuration = configurationRepository.get();
        testAccounts.forEach(account -> {
            initId(account, configuration);
        });
        configurationRepository.save(configuration);
    }

    @Override
    public boolean validateForRoute(Account account) {
        Configuration configuration = configurationRepository.get();

        boolean isFieldValid = account.isValid();

        boolean serverModeFieldValid = configuration.getServerMode() != null && configuration.getServerMode().equals(ServerMode.PUSH) ?
                account.getServerId() != null && !account.getServerId().isEmpty()
                :
                account.getRequestInterval() != null && account.getRequestInterval() > 0;

        List<Account> accounts = findAll();

        boolean sendDirDuplicate = accounts.stream().filter(acc -> acc.getSendDir() != null && account.getSendDir() != null && acc.getSendDir().equals(account.getSendDir())).count() > 1;
//        boolean getDirDuplicate = accounts.stream().filter(acc -> acc.getReceiveDir() != null && account.getReceiveDir() != null && acc.getReceiveDir().equals(account.getReceiveDir())).count() > 1;
        boolean loginDuplicate = accounts.stream().filter(acc -> acc.getLogin() != null && account.getLogin() != null && acc.getLogin().equals(account.getLogin())).count() > 1;

        return isFieldValid && serverModeFieldValid && !sendDirDuplicate && !loginDuplicate; // !getDirDuplicate
    }

    @Override
    public int validate(Account account) {
        Configuration configuration = configurationRepository.get();
        if (configuration.getServerMode() != null && configuration.getServerMode().equals(ServerMode.PUSH)) {
            if (account.getServerId() == null || account.getServerId().isEmpty())
                return AccountErrors.EMPTY_SERVER_ID;
        }else {
            if (account.getRequestInterval() == null || account.getRequestInterval() == 0)
                return AccountErrors.EMPTY_INTERVAL;
        }
        if (account.getLogin() == null || account.getLogin().isEmpty()) return AccountErrors.EMPTY_LOGIN;
        if (account.getPassword() == null || account.getPassword().isEmpty()) return AccountErrors.EMPTY_PASSWORD;
        if (account.getSendDir() == null || account.getSendDir().isEmpty()) return AccountErrors.INCORRECT_SEND_DIR;
        if (Files.notExists(Paths.get(account.getSendDir()))) return AccountErrors.INCORRECT_SEND_DIR;
        if (account.getReceiveDir() == null || account.getReceiveDir().isEmpty())
            return AccountErrors.INCORRECT_RECEIVE_DIR;
        if (Files.notExists(Paths.get(account.getReceiveDir()))) return AccountErrors.INCORRECT_RECEIVE_DIR;

        return AccountErrors.OK;
    }

    private void initId(Account account, Configuration configuration) {
        if (account.getId() != null && configuration.getAccounts().size() > 0) {
            configuration.getAccounts().forEach(acc -> {
                if (acc.getId().equals(account.getId())) {
                    BeanUtils.copyProperties(account, acc);
                }
            });
        } else {
            if (configuration.getAccounts().isEmpty()) {
                account.setId(0);
            } else {
                account.setId(configuration.getAccounts().get(configuration.getAccounts().size() - 1).getId() + 1);
            }
            configuration.getAccounts().add(account);
        }
    }
}
