package ru.alta.svd.client.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.domain.entity.ProxyServerConfig;
import ru.alta.svd.client.core.domain.entity.types.AuthType;
import ru.alta.svd.client.core.domain.entity.types.EncryptionType;
import ru.alta.svd.client.core.domain.entity.types.ServerMode;
import ru.alta.svd.client.core.main.SvdClientCoreApplication;
import ru.alta.svd.client.core.service.api.AccountService;
import ru.alta.svd.client.core.service.api.ConfigurationService;
import ru.alta.svd.client.core.validator.ConfigurationErrors;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {SvdClientCoreApplication.class})
public class ConfigurationServiceImplTest {

    private Configuration testConfig;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private AccountService accountService;


    @Before
    public void setup() throws Exception {
        this.testConfig = Configuration.builder()
                .connectionTimeout(5000)
                .socketTimeout(5000)
                .url("localhost:8080")
                .serverMode(ServerMode.TIMER)
                .encryptionType(EncryptionType.RSA)
                .serverUsername("login2")
                .proxy(ProxyServerConfig.builder()
                        .active(false)
                        .authType(AuthType.Basic)
                        .login("login")
                        .password("password")
                        .port(8080)
                        .url("url")
                        .build())
                .build();
        configurationService.saveConfiguration(testConfig);

        testConfig = configurationService.loadConfig();
        accountService.save(
                Account.builder()
                        .id(1)
                        .login("login")
                        .password("password")
                        .receiveDir("/data/testReceiveDir")
                        .requestInterval(5000)
                        .sendDir("/data/testSendDir")
                        .serverId("1")
                        .build()
        );
    }

    @Test
    public void testSaveToFile() throws Exception {
        File file = configurationService.saveToFile(testConfig);

        assertTrue(file.exists());
    }

    @Test
    public void loadConfigFile() throws IOException {
//        configurationService.saveToFile(testConfig);
//        Configuration configurationFromFile = configurationService.loadConfigFile();
//
//        assertEquals(testConfig.getConnectionTimeout(), configurationFromFile.getConnectionTimeout());
    }

    @Test
    public void testSaveFromFile(){

    }

    @Test
    public void saveConfiguration() {

        Configuration configurationDB = configurationService.loadConfig();

        assertNotNull(configurationDB);
    }

    @Test
    public void loadConfig() {
        Configuration configuration = configurationService.loadConfig();

        System.out.println(configuration);
        assertEquals(configuration, testConfig);
        assertEquals(configuration.getProxy(), testConfig.getProxy());
    }

    @Test
    public void loadConfigProxy() {
        Configuration configuration = configurationService.loadConfig();

        System.out.println(configuration);
        assertEquals(configuration.getProxy(), testConfig.getProxy());
        assertNull(configuration.getProxy().getConfiguration());
    }

    @Test
    public void entityGraphTest() {
        configurationService.loadConfig();
    }

    @Test
    public void validate() {
        assertEquals((int) Configuration.validate(testConfig), ConfigurationErrors.INCORRECT_URL);
    }

}