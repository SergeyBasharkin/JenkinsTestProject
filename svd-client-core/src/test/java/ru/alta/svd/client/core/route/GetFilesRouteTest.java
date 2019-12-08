package ru.alta.svd.client.core.route;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.alta.svd.client.core.dao.impl.CsvOperationLogRepository;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.route.consumers.GetFilesRouteConsumer;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.transport.protocol.dto.XMLFile;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = GetFilesRouteTest.class)
public class GetFilesRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "direct:from")
    private ProducerTemplate endpoint;

    private GetFilesRouteConsumer consumer;
    private OperationLogService operationLogService;

    @Before
    public void setup() throws Exception {

    }

    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        Properties properties =
                Optional.ofNullable(super.useOverridePropertiesWithPropertiesComponent())
                        .orElse(new Properties());
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            properties.load(new FileReader(classLoader.getResource("application.properties").getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        consumer = mock(GetFilesRouteConsumer.class);
        when(consumer.getAccount()).thenReturn(Account.builder().login("TestLogin").receiveDir("TestReceived").build());
        doNothing().when(consumer).checkFiles(any());
        doCallRealMethod().when(consumer).xmlToFile(any(), any());
        when(consumer.recognizeSaveFileEndpoint()).thenReturn("mock:saveFile");
        operationLogService = mock(OperationLogService.class);
        return new GetFilesRoute(operationLogService, consumer);
    }

    @Test
    @DirtiesContext
    public void testGetFiles() throws Exception {
        XMLFile.File file = generateFile();

        MockEndpoint saveFile = getMockEndpoint("mock:saveFile");
        saveFile.expectedBodiesReceived((Object) "Hello".getBytes());

        this.endpoint.sendBody("direct:getFileToTestLogin", file);

        assertMockEndpointsSatisfied();

        verify(consumer, times(1)).checkFiles(any());
        verify(consumer, times(1)).xmlToFile(any(), any());
    }

    @Test
    @DirtiesContext
    public void checkFiles() throws Exception {
        XMLFile.File file = generateFile();

        doCallRealMethod().when(consumer).checkFiles(any());
        MockEndpoint saveFile = getMockEndpoint("mock:saveFile");
        saveFile.expectedBodiesReceived((Object) "Hello".getBytes());

        boolean checkFiles = false;
        try {
            this.endpoint.sendBody("direct:getFileToTestLogin", file);
        } catch (Exception e) {
            checkFiles = true;
        }

        assertTrue(checkFiles);
        assertMockEndpointsSatisfied();
    }

    @Test
    @DirtiesContext
    public void testExceptionLog() throws InterruptedException {
        XMLFile.File file = generateFile();

        MockEndpoint saveFile = getMockEndpoint("mock:saveFile");
        saveFile.whenAnyExchangeReceived(exchange -> {
            throw new Exception();
        });

        boolean exception = false;
        try {
            this.endpoint.sendBody("direct:getFileToTestLogin", file);
        } catch (Throwable e) {
            exception = true;
        }

        verify(operationLogService, times(1)).log(any());
        assertTrue(exception);
        assertMockEndpointsSatisfied();
    }

    private XMLFile.File generateFile() {
        XMLFile.File file = new XMLFile.File();
        file.setStatId("123");
        file.setLogin("login123");
        file.setId("11");
        file.setContent("Hello".getBytes());
        return file;
    }
}