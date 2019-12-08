package ru.alta.svd.client.rest.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.service.api.AccountService;
import ru.alta.svd.client.core.service.api.ConfigurationService;
import ru.alta.svd.client.rest.dto.AccountObject;
import ru.alta.svd.client.rest.exception.AccountNotFoundException;
import ru.alta.svd.client.rest.exception.MultipartNotFoundException;
import ru.alta.svd.client.rest.util.FileHelper;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolSendParams;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author pechenko
 */
@RestController
@AllArgsConstructor
@RequestMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AccountController {

    private final AccountService accountService;
    private final ConfigurationService configurationService;

    @ApiOperation(value = "Список учетных записей")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<AccountObject> list() {
        Configuration configuration = configurationService.loadConfig();
        if (configuration == null) return new ArrayList<>();
        List<Account> accounts = configuration.getAccounts();
        return accounts.stream().map(account -> {
            AccountObject obj = new AccountObject();
            BeanUtils.copyProperties(account, obj);
            obj.setTransportError(accountService.checkTransportError(account));
            obj.setValidationError(accountService.validate(account));
            obj.setDuplicateError(accountService.checkDuplicates(account));
            if (account.getRequestInterval() != null) obj.setRequestInterval(account.getRequestInterval() / 1000);
            if (account.getMsgType() != null) obj.setMsgType(account.getMsgType().toString());
            clearNullAccountFields(obj);
            return obj;
        }).collect(Collectors.toList());
    }

    @ApiOperation(value = "Создание/обновление учетной записи")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void save(@RequestBody AccountObject accountObject) throws Exception {
        Account account = new Account();
        BeanUtils.copyProperties(accountObject, account);
        if (account.getRequestInterval() != null) account.setRequestInterval(accountObject.getRequestInterval() * 1000);
        try {
            account.setMsgType(SvdTransportProtocolSendParams.MsgTypeEnum.valueOf(accountObject.getMsgType()));
            if (account.getServerId() == null || account.getServerId().isEmpty()) {
                account.setServerId(UUID.randomUUID().toString());
            }
        } catch (RuntimeException ignored) {
            log.warn("mstType unrecognized");
        }
        accountService.save(account);
    }

    @ApiOperation(value = "Удаление учетной записи")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer id) throws Exception {
        Account account = accountService.findOne(id).orElseThrow(AccountNotFoundException::new);
        accountService.delete(account);
    }

    @ApiOperation(value = "Загрузка настроек из файла")
    @RequestMapping(value = "/file/save", method = RequestMethod.POST)
    public void saveFromFile(@RequestParam(name = "file", required = false) MultipartFile multipart) throws Exception {
        if (multipart == null) throw new MultipartNotFoundException();
        accountService.saveFromFile(FileHelper.multipartToFile(multipart));
    }

    private void clearNullAccountFields(AccountObject obj) {
        if (obj.getLogin() == null) obj.setLogin("");
        if (obj.getPassword() == null) obj.setPassword("");
        if (obj.getServerId() == null) obj.setServerId("");
        if (obj.getSendDir() == null) obj.setSendDir("");
        if (obj.getReceiveDir() == null) obj.setReceiveDir("");
        if (obj.getRequestInterval() == null) obj.setRequestInterval(0);
        if (obj.getMsgType() == null) obj.setMsgType("");
    }
}
