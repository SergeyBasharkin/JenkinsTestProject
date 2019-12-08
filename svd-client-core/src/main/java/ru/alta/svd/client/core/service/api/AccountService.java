package ru.alta.svd.client.core.service.api;

import ru.alta.svd.client.core.domain.entity.Account;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    void delete(Account account) throws Exception;
    List<Account> findAccounts();
    void save(Account account) throws Exception;

//    void updateConfigAndRegisterRoutes(List<Account> accounts, Configuration configuration);

    Optional<Account> findOne(Integer id);

    void saveFromFile(File file) throws Exception;

    int validate(Account account);

    int checkTransportError(Account account);

    Integer checkDuplicates(Account account);

    boolean validateForRoute(Account account);
//    void startPushFilesRoute(Account account, Configuration configuration) throws Exception;

}
