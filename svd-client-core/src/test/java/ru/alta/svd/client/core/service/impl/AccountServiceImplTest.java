package ru.alta.svd.client.core.service.impl;

import org.apache.camel.CamelContext;
import org.ini4j.Ini;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.domain.entity.ProxyServerConfig;
import ru.alta.svd.client.core.domain.entity.types.AuthType;
import ru.alta.svd.client.core.domain.entity.types.EncryptionType;
import ru.alta.svd.client.core.domain.entity.types.OperationType;
import ru.alta.svd.client.core.domain.entity.types.ServerMode;
import ru.alta.svd.client.core.dao.repository.AccountRepository;
import ru.alta.svd.client.core.main.SvdClientCoreApplication;
import ru.alta.svd.client.core.service.api.AccountService;
import ru.alta.svd.client.core.service.api.ConfigurationService;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.client.core.validator.AccountErrors;
import ru.alta.svd.client.core.validator.OperationLogErrors;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolSendParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = SvdClientCoreApplication.class)
@PropertySource("classpath*:application.properties")
public class AccountServiceImplTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private RoutesServiceImpl routesService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private OperationLogService operationLogService;

    @Before
    public void setup() throws Exception {
        destroy();
        configurationService.saveConfiguration(Configuration.builder()
                .connectionTimeout(5000)
                .socketTimeout(5000)
                .url("http://localhost:8080")
                .serverMode(ServerMode.TIMER)
                .encryptionType(EncryptionType.RSA)
                .pushServerUrl("")
                .serverUsername("login2")
                .proxy(ProxyServerConfig.builder()
                        .active(false)
                        .authType(AuthType.Basic)
                        .login("login")
                        .password("password")
                        .port(8080)
                        .url("url")
                        .build())
                .build()
        );
        accountService.save(Account.builder()
                .login("login")
                .password("pass")
                .receiveDir("./data/test/receive")
                .sendDir("./data/test/send")
                .serverId("1")
                .requestInterval(10)
                .msgType(SvdTransportProtocolSendParams.MsgTypeEnum.DATA)
                .build()
        );
        accountService.save(Account.builder()
                .login("login1")
                .password("pass")
                .receiveDir("./data/test1/receive")
                .sendDir("./data/test1/send")
                .serverId("1")
                .requestInterval(10)
                .msgType(SvdTransportProtocolSendParams.MsgTypeEnum.DATA)
                .build()
        );

    }

    @Test
    public void testValidate(){
        Configuration conf = configurationService.loadConfig();
        conf.setServerMode(ServerMode.PUSH);
        configurationService.saveConfiguration(conf);

        assertEquals(9, accountRepository.validate(Account.builder().build()));
    }

    @After
    public void destroy() {
        operationLogService.clear();
        configurationService.destroy();
    }

    @Test
    public void delete() throws Exception {
        Account account = accountService.findOne(0).get();

        assertEquals("login", account.getLogin());

        accountService.delete(account);

        assertFalse(accountRepository.findOne(0).isPresent());

        accountService.save(Account.builder().login("login2").build());
    }

    @Test
    public void destDeleteRoute() throws Exception {
        Account account = accountService.findOne(0).get();

        assertNotNull(camelContext.getRoute("route-get-login"));
        assertNotNull(camelContext.getRoute("route-push-login"));

        accountService.delete(account);

        assertNull(camelContext.getRoute("route-get-login"));
        assertNull(camelContext.getRoute("route-push-login"));
    }

    @Test
    public void findAccounts() throws Exception {
        List<Account> testAccounts = Arrays.asList(
                Account.builder().login("1").serverId("1").build(),
                Account.builder().login("2").serverId("1").build(),
                Account.builder().login("3").serverId("1").build()
        );
        accountRepository.save(testAccounts);

        List<Account> accounts = accountService.findAccounts();
        assertEquals(5, accounts.size());
        assertTrue(accounts.stream().allMatch(account -> account.getServerId().equals("1")));
    }

    @Test
    public void save() throws Exception {
        Account account = Account.builder().login("testSave").build();
        accountService.save(account);

        assertTrue(accountRepository.findOne(2).isPresent());
        assertEquals(accountRepository.findOne(2).get().getLogin(), account.getLogin());
    }

    @Test
    public void update() throws Exception {
        Optional<Account> account = accountRepository.findOne(0);

        assertTrue(account.isPresent());
        assertNotNull(camelContext.getRoute("route-push-"+account.get().getLogin()));
        account.get().setLogin("updatedTestSave");
        accountService.save(account.get());

        assertNull(account.get().getConfiguration());
        assertTrue(accountRepository.findOne(0).isPresent());
        assertEquals(accountRepository.findOne(0).get(), account.get());
        assertNotNull(camelContext.getRoute("route-push-"+ account.get().getLogin()));
        assertNull(camelContext.getRoute("route-push-login"));

    }

    @Test
    public void updateStopRoutes() throws Exception {
        Optional<Account> account = accountRepository.findOne(0);

        assertTrue(account.isPresent());
        assertNotNull(camelContext.getRoute("route-push-"+account.get().getLogin()));

//        routesService.stopOldRoutes(account.get());
        account.get().setLogin("login1");
        accountService.save(account.get());

        assertNull(account.get().getConfiguration());
        assertTrue(accountRepository.findOne(0).isPresent());
        assertEquals(accountRepository.findOne(0).get(), account.get());
        assertNotNull(camelContext.getRoute("route-push-"+ account.get().getLogin()));
        assertNull(camelContext.getRoute("route-push-login"));

    }

    @Test
    public void testUpdate() throws Exception {
        Optional<Account> account = accountRepository.findOne(0);

        assertTrue(account.isPresent());
        account.get().setMsgType(SvdTransportProtocolSendParams.MsgTypeEnum.ED);
        accountService.save(account.get());

        assertNull(account.get().getConfiguration());
        assertTrue(accountRepository.findOne(0).isPresent());
        assertEquals(accountRepository.findOne(0).get(), account.get());
    }

    @Test
    public void updateSendDir() throws Exception {
        Optional<Account> account = accountRepository.findOne(0);

        assertTrue(account.isPresent());
        assertNotNull(camelContext.getRoute("route-push-"+account.get().getLogin()));

        account.get().setSendDir("./data/test/updateSend");
        accountService.save(account.get());

        assertNull(account.get().getConfiguration());
        assertTrue(accountRepository.findOne(0).isPresent());
        assertEquals(accountRepository.findOne(0).get(), account.get());
    }


    @Test
    public void testRouteRegister() throws Exception {
        Account account = Account.builder().login("testSave").password("pass").sendDir("./data/test/testSaveSend").receiveDir("./data/test/testSaveReceive").requestInterval(10).build();
        accountService.save(account);

        assertNotNull(camelContext.getRoute("route-get-testSave"));
        assertNotNull(camelContext.getRoute("route-push-testSave"));
    }

    @Test
    public void findOne() throws Exception {
        Optional<Account> account = accountService.findOne(1);
        System.out.println(account.toString());
        assertTrue(account.isPresent());
        assertTrue(account.get().getLogin().equals("login1"));
    }

    @Test
    public void saveFromFile() throws Exception {
        Ini ini = new Ini();
        ini.add("ED", "HTTPLogin", "TestLogin");
        ini.add("ED", "HttpPassword", "TestPassword");

        File zipFile = File.createTempFile("test", "config.zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        ZipEntry e = new ZipEntry("config.ini");
        out.putNextEntry(e);

        ini.store(out);
        out.closeEntry();

        out.close();

        accountService.saveFromFile(zipFile);

        assertTrue(accountService.findOne(2).get().getLogin().equals("TestLogin"));
    }


    @Test
    public void validateOk() throws IOException {
        Path pathSend = Paths.get("./data/testSendDir");
        Path pathReceive = Paths.get("./data/testReceiveDir");
        Files.createDirectories(pathSend);
        Files.createDirectories(pathReceive);
        Account account = Account.builder()
                .login("login")
                .password("password")
                .requestInterval(500)
                .sendDir("./data/testSendDir")
                .receiveDir("./data/testReceiveDir")
                .build();

        assertEquals(accountService.validate(account), AccountErrors.OK);
    }

    @Test
    public void validateEmptyLogin() {
        Account account = Account.builder()
                .password("password")
                .requestInterval(500)
                .sendDir("./data/testSendDir")
                .receiveDir("./data/testReceiveDir")
                .build();

        assertEquals(accountService.validate(account), AccountErrors.EMPTY_LOGIN);
    }

    @Test
    public void validateEmptyPassword() {
        Account account = Account.builder()
                .login("login")
                .requestInterval(500)
                .sendDir("/data/testSendDir")
                .receiveDir("/data/testReceiveDir")
                .build();

        assertEquals(accountService.validate(account), AccountErrors.EMPTY_PASSWORD);
    }

    @Test
    public void validateEmptyInterval() {
        Account account = Account.builder()
                .login("login")
                .password("password")
                .sendDir("/data/testSendDir")
                .receiveDir("/data/testReceiveDir")
                .requestInterval(0)
                .build();

        assertEquals(accountService.validate(account), AccountErrors.EMPTY_INTERVAL);
    }

    @Test
    public void validateSendDir() throws IOException {
        Path pathSend = Paths.get("./data/testSendDir");
        Path pathReceive = Paths.get("./data/testReceiveDir");
        Files.createDirectories(pathSend);
        Files.createDirectories(pathReceive);
        Account account = Account.builder()
                .login("login")
                .password("password")
                .requestInterval(500)
                .sendDir("/data/notExist")
                .receiveDir("./data/testReceiveDir")
                .build();

        assertEquals(accountService.validate(account), AccountErrors.INCORRECT_SEND_DIR);
    }

    @Test
    public void validateReceiveDir() throws IOException {
        Path pathSend = Paths.get("./data/testSendDir");
        Path pathReceive = Paths.get("./data/testReceiveDir");
        Files.createDirectories(pathSend);
        Files.createDirectories(pathReceive);
        Account account = Account.builder()
                .login("login")
                .password("password")
                .requestInterval(500)
                .sendDir("./data/testSendDir")
                .receiveDir("/data/notExist")
                .build();

        assertEquals(accountService.validate(account), AccountErrors.INCORRECT_RECEIVE_DIR);
    }

    @Test
    public void validateReceiveDirEmpty() throws IOException {
        Account account = Account.builder()
                .login("login")
                .password("password")
                .requestInterval(500)
                .sendDir("./data/testSendDir")
                .receiveDir("")
                .build();

        assertEquals(accountService.validate(account), AccountErrors.INCORRECT_RECEIVE_DIR);
    }

    @Test
    public void checkTransportError() {
        Account account = Account.builder()
                .login("login")
                .password("password")
                .requestInterval(500)
                .sendDir("/data/testSendDir")
                .receiveDir("/data/notExist")
                .build();

        assertEquals(accountService.checkTransportError(account), OperationLogErrors.OK);

        operationLogService.log(OperationLog.builder()
                .date(LocalDateTime.of(2030, 1, 1, 0, 0))
                .login(account.getLogin())
                .type(OperationType.SEND)
                .fileName("CamelFileName")
                .fileSize(1L)
                .period(0L)
                .exceptionMessage("")
                .resultState(5)
                .resultMessage("test")
                .build());

        assertEquals(accountService.checkTransportError(account), OperationLogErrors.SEND_ERROR);


        operationLogService.log(OperationLog.builder()
                .date(LocalDateTime.of(2030, 1, 2, 0, 0))
                .login(account.getLogin())
                .type(OperationType.RECEIVE)
                .fileName("CamelFileName")
                .fileSize(1L)
                .period(0L)
                .exceptionMessage("")
                .resultState(5)
                .resultMessage("test")
                .build());
        assertEquals(accountService.checkTransportError(account), OperationLogErrors.RECEIVE_ERROR);

        operationLogService.log(OperationLog.builder()
                .date(LocalDateTime.of(2030, 1, 3, 0, 0))
                .login(account.getLogin())
                .type(OperationType.DELETE)
                .fileName("CamelFileName")
                .fileSize(1L)
                .period(0L)
                .exceptionMessage("")
                .resultState(5)
                .resultMessage("test")
                .build());
        assertEquals(accountService.checkTransportError(account), OperationLogErrors.DELETE_ERROR);
    }

    private void createConfig() {
        configurationService.saveConfiguration(Configuration.builder()
                .connectionTimeout(5000)
                .socketTimeout(5000)
                .url("localhost:8080")
                .serverMode(ServerMode.TIMER)
                .encryptionType(EncryptionType.RSA)
                .pushServerUrl("")
                .serverUsername("login2")
                .proxy(ProxyServerConfig.builder()
                        .active(false)
                        .authType(AuthType.Basic)
                        .login("login")
                        .password("password")
                        .port(8080)
                        .url("url")
                        .build())
                .build()
        );
    }
}