package ru.alta.svd.client.rest.controller;

import org.apache.camel.CamelContext;
import org.ini4j.Ini;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.alta.svd.client.core.dao.repository.AccountRepository;
import ru.alta.svd.client.core.dao.repository.OperationLogRepository;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.domain.entity.ProxyServerConfig;
import ru.alta.svd.client.core.domain.entity.types.AuthType;
import ru.alta.svd.client.core.domain.entity.types.EncryptionType;
import ru.alta.svd.client.core.domain.entity.types.ServerMode;
import ru.alta.svd.client.core.service.api.ConfigurationService;
import ru.alta.svd.client.rest.dto.AccountObject;
import ru.alta.svd.client.rest.exception.AccountNotFoundException;
import ru.alta.svd.client.rest.exception.MultipartNotFoundException;
import ru.alta.svd.client.rest.main.SvdClientRestApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {SvdClientRestApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AccountControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CamelContext camelContext;

    @Value("http://localhost:${local.server.port}/account")
    private String appPath;

    private Account testAccount;

    @Before
    public void setup() {
        configurationService.destroy();
        operationLogRepository.clearByLogin("testLogin");
        testAccount = Account.builder()
                .login("testLogin")
                .password("pass")
                .sendDir("./test/send")
                .receiveDir("./test/receive")
                .build();
        accountRepository.save(testAccount);
    }

    @After
    public void destroy() {
        configurationService.destroy();
        operationLogRepository.clearByLogin("testLogin");
    }

//    @Test
//    public void testListValidation() {
//        AccountObject[] list = restTemplate.getForObject(appPath + "/list", AccountObject[].class);
//        for (int i = 0; i < list.length; i++) {
//            assertEquals(list[i].getValidationError(), Integer.valueOf(4));
//            assertEquals(list[i].getDuplicateError(), Integer.valueOf(0));
//            assertEquals(list[i].getTransportError(), Integer.valueOf(0));
//        }
//    }

//    @Test
//    public void testListValidationNoErrors() throws IOException {
//        Files.createDirectories(Paths.get(testAccount.getReceiveDir()));
//        Files.createDirectories(Paths.get(testAccount.getSendDir()));
//        AccountObject[] list = restTemplate.getForObject(appPath + "/list", AccountObject[].class);
//        for (int i = 0; i < list.length; i++) {
//            assertEquals(list[i].getValidationError(), Integer.valueOf(0));
//            assertEquals(list[i].getDuplicateError(), Integer.valueOf(0));
//            assertEquals(list[i].getTransportError(), Integer.valueOf(0));
//        }
//
//        Files.deleteIfExists(Paths.get(testAccount.getReceiveDir()));
//        Files.deleteIfExists(Paths.get(testAccount.getSendDir()));
//        Files.deleteIfExists(Paths.get(testAccount.getReceiveDir()).getParent());
//    }

    @Test
    public void list() throws Exception {
        AccountObject[] list = restTemplate.getForObject(appPath + "/list", AccountObject[].class);
        assertEquals(1, list.length);
        assertEquals(list[0].getLogin(), "testLogin");

    }

    @Test
    public void save() throws Exception {
        AccountObject testAccountObject = AccountObject.builder()
                .login("newLogin")
                .msgType("DATA")
                .build();
        restTemplate.postForLocation(appPath + "/save", testAccountObject);

        AccountObject[] accounts = restTemplate.getForObject(appPath + "/list", AccountObject[].class);

        assertEquals(2, accounts.length);
        assertTrue(Arrays.stream(accounts).anyMatch(account -> account.getLogin().equals(testAccountObject.getLogin())));
    }

    @Test
    public void update() throws Exception {
        File fileRec = new File(testAccount.getReceiveDir());
        File fileSend = new File(testAccount.getSendDir());
        fileRec.mkdirs();
        fileSend.mkdirs();
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
                .build());

        AccountObject[] accounts = restTemplate.getForObject(appPath + "/list", AccountObject[].class);

        assertEquals(accounts.length, 1);
        assertNotNull(accounts[0]);
        assertNotNull(camelContext.getRoute("route-push-"+accounts[0].getLogin()));
        accounts[0].setLogin("updatedTestSave");

        restTemplate.postForLocation(appPath + "/save", accounts[0]);

        accounts = restTemplate.getForObject(appPath + "/list", AccountObject[].class);

        assertEquals(accounts.length, 1);
        assertNotNull(camelContext.getRoute("route-push-"+ accounts[0].getLogin()));
        assertNull(camelContext.getRoute("route-push-testLogin"));

        configurationService.destroy();
        operationLogRepository.clearByLogin("updatedTestSave");

        fileRec.deleteOnExit();
        fileSend.deleteOnExit();

    }

    @Test
    public void saveException() throws Exception {
        AccountObject testAccountObject = AccountObject.builder()
                .login("newLogin")
                .msgType("Incorrect")
                .build();
        restTemplate.postForLocation(appPath + "/save", testAccountObject);

        List<Account> accounts = accountRepository.findAll();

        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().anyMatch(account -> account.getLogin().equals(testAccountObject.getLogin())));
    }

    @Test
    public void saveIncorrect() {
        AccountObject incorrectAccount = AccountObject.builder()
                .login("login")
                .password("pass")
                .sendDir("./data/send")
                .receiveDir("./data/receive")
                .build();

        configurationService.saveConfiguration(
                Configuration.builder()
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

        restTemplate.postForLocation(appPath + "/save", incorrectAccount);

        AccountObject[] list = restTemplate.getForObject(appPath + "/list", AccountObject[].class);

        for (int i = 0; i < list.length; i++) {
            if (list[i].getLogin().equals("login")) {
                assertEquals(list[i].getValidationError(), Integer.valueOf(3));
            }
        }
    }

    @Test
    public void delete() throws Exception {
        restTemplate.delete(appPath + "/delete/" + 0);

        List<Account> accounts = accountRepository.findAll();

        assertTrue(accounts.isEmpty());
        assertTrue(accounts.stream().noneMatch(account -> account.getLogin().equals("testLogin")));
    }

    @Test
    public void deleteUnknown() throws Exception {
        ResponseEntity<String> responseEntity = restTemplate.exchange(appPath + "/delete/" + 322, HttpMethod.DELETE, null, String.class);
        assertEquals(responseEntity.getStatusCodeValue(), 404);
        new AccountNotFoundException();
        new MultipartNotFoundException();
    }

    @Test
    public void saveFromFile() throws IOException {
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
        FileSystemResource fileSystemResource = new FileSystemResource(zipFile);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate
                .postForEntity(appPath + "/file/save", requestEntity, String.class);
        assertTrue(accountRepository.findAll().stream().anyMatch(account -> account.getLogin().equals("TestLogin")));
    }
}