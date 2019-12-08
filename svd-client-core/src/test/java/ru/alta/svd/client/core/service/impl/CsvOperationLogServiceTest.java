package ru.alta.svd.client.core.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.domain.entity.types.OperationType;
import ru.alta.svd.client.core.dto.OperationFilter;
import ru.alta.svd.client.core.dto.Page;
import ru.alta.svd.client.core.dto.PageRequest;
import ru.alta.svd.client.core.main.SvdClientCoreApplication;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.client.core.util.OperationLogFaker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {SvdClientCoreApplication.class})
public class CsvOperationLogServiceTest {

    @Qualifier("csvLog")
    @Autowired
    private OperationLogService operationLogService;

    @Before
    public void setup() {
        operationLogService.clearByLogin("testLogin");
    }

    @Test
    public void logSend() {
        OperationLog log = OperationLog.builder()
                .date(LocalDateTime.now())
                .type(OperationType.SEND)
                .login("testLogin")
                .fileName("testFile")
                .fileSize(123L)
                .period(100L)
                .resultState(1)
                .resultMessage("test message")
                .exceptionMessage("asdsadasads asdsa aa")
                .build();
        operationLogService.log(log);

        File file = Paths.get(
                "." + File.separator +
                        "logs" + File.separator +
                        log.getLogin() + File.separator +
                        "send.csv").toFile();
        assertTrue(file.exists());

        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, Charset.forName("UTF-8"))) {
            String line = reader.readLine();
            assertEquals(log, OperationLog.fromCsv(line));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void test() {
        operationLogService.log(OperationLog.builder()
                .login("test1")
                .date(LocalDateTime.now())
                .type(OperationType.RECEIVE)
                .build());
        operationLogService.log(OperationLog.builder()
                .login("test2")
                .date(LocalDateTime.now())
                .type(OperationType.SEND)
                .build());
        OperationFilter filter1 = new OperationFilter();
        OperationFilter filter2 = new OperationFilter();
        filter1.setLogin("test1");
        filter1.setLogType(OperationFilter.LogType.RECEIVE);
        filter2.setLogin("test2");
        filter2.setLogType(OperationFilter.LogType.SEND);
        Page<OperationLog> operationLog = operationLogService.findBy(filter1, new PageRequest());
        Page<OperationLog> operationLog2 = operationLogService.findBy(filter2, new PageRequest());

        System.out.println();
    }

    @Test
    public void findBy() {
        generateLogs(106);
        OperationFilter operationFilter = new OperationFilter();
        operationFilter.setLogin("testLogin");
        operationFilter.setLogType(OperationFilter.LogType.RECEIVE);
        Page<OperationLog> page = operationLogService.findBy(operationFilter, new PageRequest(0, 20));
        assertEquals(page.getContent().size(), 20);
        PageRequest pageRequest5 = new PageRequest();
        pageRequest5.setPage(5);
        pageRequest5.setSize(20);
        Page<OperationLog> page5 = operationLogService.findBy(operationFilter, pageRequest5);
        assertEquals(page5.getContent().size(), 6);

        boolean serialize = true;

        try {
            String json = new ObjectMapper().writeValueAsString(page5.getContent().get(0));
            new ObjectMapper().readValue(json, OperationLog.class);
        } catch (JsonProcessingException e) {
            serialize = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(serialize);
    }

    @Test
    @Ignore
    public void findLastErrorByLogin() {
        assertNull(operationLogService.findLastErrorByLogin("testLogin"));
        OperationLog log = OperationLogFaker.get();
        System.out.println(log.toString());
        log.setLogin("testLogin");
        log.setFileName("1");
        log.setType(OperationType.RECEIVE);
        operationLogService.log(log);

        OperationLog log2 = OperationLogFaker.get();
        log2.setLogin("testLogin");
        log2.setFileName("2");
        log2.setType(OperationType.SEND);
        operationLogService.log(log2);

        assertEquals(operationLogService.findLastErrorByLogin("testLogin").getFileName(), "2");

    }

    @Test
    @Ignore
    public void findLastErrorByLoginReceive() {
        assertNull(operationLogService.findLastErrorByLogin("testLogin"));
        OperationLog log2 = OperationLogFaker.get();
        log2.setLogin("testLogin");
        log2.setFileName("2");
        log2.setType(OperationType.SEND);
        operationLogService.log(log2);

        OperationLog log = OperationLogFaker.get();
        log.setLogin("testLogin");
        log.setFileName("1");
        log.setType(OperationType.RECEIVE);
        operationLogService.log(log);

        assertEquals(operationLogService.findLastErrorByLogin("testLogin").getFileName(), "1");
    }

    @Test
    public void clear() {
    }

    @Test
    public void clearFired() {
        toArchive();
        operationLogService.clearFired(LocalDateTime.of(2019, 2, 5, 0, 0));
        assertEquals(Objects.requireNonNull(Paths.get("./logs/testLogin").toFile().listFiles()).length, 3);
    }

    @Test
    public void toArchive() {
        OperationLog log = OperationLogFaker.get();
        log.setDate(LocalDateTime.of(2019, 2, 3, 0, 0));
        log.setLogin("testLogin");
        operationLogService.log(log);
        log.setDate(LocalDateTime.of(2019, 2, 4, 0, 0));
        operationLogService.log(log);
        log.setDate(LocalDateTime.of(2019, 2, 5, 0, 0));
        operationLogService.log(log);

        log.setDate(LocalDateTime.of(2019, 2, 6, 0, 0));
        operationLogService.log(log);
    }

    @Test
    @Ignore
    public void loadTesting() {
        OperationFilter operationFilter = new OperationFilter();
        operationFilter.setLogin("testLogin");
        operationFilter.setLogType(OperationFilter.LogType.RECEIVE);

//        generateLogs(1_000_000);
        long start = System.currentTimeMillis();
        Page<OperationLog> page = operationLogService.findBy(operationFilter, new PageRequest(0, 20));
        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        Page<OperationLog> page1000 = operationLogService.findBy(operationFilter, new PageRequest(10000, 20));
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(page1000.getContent().size());

    }

    private void generateLogs(int size) {
        for (int i = 0; i < size; i++) {
            OperationLog log = OperationLogFaker.get();
            log.setLogin("testLogin");
            log.setType(OperationType.RECEIVE);
            operationLogService.log(log);
            System.out.println(i);
        }
    }

}