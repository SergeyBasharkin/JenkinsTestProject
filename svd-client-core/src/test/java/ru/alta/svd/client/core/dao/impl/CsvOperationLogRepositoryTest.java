package ru.alta.svd.client.core.dao.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.main.SvdClientCoreApplication;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {SvdClientCoreApplication.class})
public class CsvOperationLogRepositoryTest {

    @Autowired
    private CsvOperationLogRepository csvOperationLogRepository;

    @Test
    public void testSaveNull(){
        csvOperationLogRepository.save(OperationLog.builder().build());
    }

}