package ru.alta.svd.client.core.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alta.svd.client.core.dto.XMLFile;
import ru.alta.svd.client.core.main.SvdClientCoreApplication;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = {SvdClientCoreApplication.class})
public class OperationLogServiceImplTest {

    @Test
    public void testXMLFile() throws IllegalAccessException {
        XMLFile xmlFile = new XMLFile();
        XMLFile.OperationResult operationResult = new XMLFile.OperationResult();
        XMLFile.File file = new XMLFile.File();
        file.setContent(new byte[5]);
        file.setId("1");
        file.setLogin("login");
        file.setStatId("1");
        operationResult.setMessage("test");
        operationResult.setState(5);
        xmlFile.setDays("1");
        xmlFile.setPay("1");
        xmlFile.setPayRub("1");
        xmlFile.setFile(Collections.singletonList(file));
        xmlFile.setOperationResult(operationResult);

        assertNotNull(xmlFile);
    }
}