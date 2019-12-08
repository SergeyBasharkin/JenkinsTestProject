package ru.alta.svd.client.core.dao.repository;

import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.Configuration;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account findById(Integer id);

    void delete(Account account);

    List<Account> findAll();

    void save(Account account);

    Optional<Account> findOne(Integer id);

    void save(List<Account> testAccounts);

    boolean validateForRoute(Account account);

    int validate(Account account);
}
