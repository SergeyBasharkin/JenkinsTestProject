package ru.alta.svd.client.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
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
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.domain.entity.ProxyServerConfig;
import ru.alta.svd.client.core.domain.entity.types.AuthType;
import ru.alta.svd.client.core.domain.entity.types.EncryptionType;
import ru.alta.svd.client.core.domain.entity.types.ServerMode;
import ru.alta.svd.client.core.service.api.ConfigurationService;
import ru.alta.svd.client.rest.dto.ConfigObject;
import ru.alta.svd.client.rest.main.SvdClientRestApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {SvdClientRestApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ConfigControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ConfigurationService configurationService;

    @Value("http://localhost:${local.server.port}/config")
    private String appPath;

    private Configuration testConfig;

    @Before
    public void setup() {
        testConfig = configurationService.saveConfiguration(
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
    }


    @After
    public void destroy() {
        configurationService.destroy();
    }


    @Test
    public void get() throws Exception {
        ConfigObject configuration = restTemplate.getForObject(appPath + "/get", ConfigObject.class);

        assertEquals(configuration.getUrl(), testConfig.getUrl());
        assertEquals(configuration.getServerUsername(), testConfig.getServerUsername());
    }

    @Test
    public void save() throws Exception {
        configurationService.destroy();
        ConfigObject configObject = new ConfigObject();
        BeanUtils.copyProperties(testConfig, configObject);
        BeanUtils.copyProperties(testConfig.getProxy(), configObject.getProxy());
        restTemplate.postForLocation(appPath + "/save", configObject);
        assertEquals(configurationService.loadConfig().getUrl(), testConfig.getUrl());
    }

    @Test
    public void saveToFile() {
        ResponseEntity<String> response = restTemplate
                .getForEntity(appPath + "/file/save", null, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void fondOldConfig() {
        ResponseEntity<String> response = restTemplate
                .getForEntity(appPath + "/old/list", null, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void saveFromFile() throws IOException {
        File file = Files.createTempFile("test", ".xml").toFile();
        new XmlMapper().writeValue(file, testConfig);

        FileSystemResource fileSystemResource = new FileSystemResource(file);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(appPath + "/file/load", requestEntity, String.class);
        assertEquals(response.getStatusCode().value(), 200);
    }

    @Test
    public void testConfigObj(){
        ConfigObject configObject = new ConfigObject(
                0L,
                "test",
                "test",
                10,
                10,
                EncryptionType.RSA,
                ServerMode.TIMER,
                "test",
                10,
                new ConfigObject.ProxyServerConfigObject(),
                10
        );

        assertNotNull(configObject);
    }
}