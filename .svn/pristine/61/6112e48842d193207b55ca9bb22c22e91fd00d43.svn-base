package ru.alta.svd.client.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alta.svd.client.core.dao.repository.OperationLogRepository;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.domain.entity.types.OperationType;
import ru.alta.svd.client.core.dto.Page;
import ru.alta.svd.client.rest.main.SvdClientRestApplication;

import java.nio.file.Paths;
import java.time.LocalDateTime;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {SvdClientRestApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OperationControllerTest {


    @Value("http://localhost:${local.server.port}/operation")
    private String appPath;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OperationLogRepository operationLogRepository;

    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        for (int i = 0; i < 5; i++) {
            operationLogRepository.save(OperationLog.builder()
                    .login(String.valueOf(i))
                    .resultState(i)
                    .date(LocalDateTime.of(2018, 9, 9, 9, i))
                    .type(OperationType.SEND)
                    .build()
            );
        }
    }

    @After
    public void after() {
        operationLogRepository.clear();
    }

    @Test
    public void list() throws Exception {
        ParameterizedTypeReference<Page<OperationLog>> responseType = new ParameterizedTypeReference<Page<OperationLog>>() {};

        ResponseEntity<Page<OperationLog>> responseEntity = restTemplate.exchange(appPath + "/list?login=0&logType=SEND", HttpMethod.GET, null, responseType);
        assertEquals(1, responseEntity.getBody().getContent().size());

    }

    @Test
    public void badList() throws Exception {

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(appPath + "/list", String.class);
        assertEquals(400, responseEntity.getStatusCodeValue());

    }

    @Test
    public void clearLog(){
        assertEquals(Paths.get("./logs/").toFile().listFiles().length, 5);

        restTemplate.delete(appPath + "/clear");

        assertFalse(Paths.get("./logs/").toFile().exists());
    }

}