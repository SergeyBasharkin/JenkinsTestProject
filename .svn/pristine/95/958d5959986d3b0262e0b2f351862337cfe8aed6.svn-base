package ru.alta.svd.client.core.route;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.route.consumers.PushFileRouteConsumer;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolSendParams;
import ru.alta.svd.transport.protocol.service.api.SvdTransportProtocolService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = GetFilesRouteTest.class)
public class PushFileRouteTest extends CamelTestSupport {
    @EndpointInject(uri = "direct:from")
    private ProducerTemplate endpoint;

    SvdTransportProtocolService svdTransportProtocolService;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        svdTransportProtocolService = mock(SvdTransportProtocolService.class);
        doNothing().when(svdTransportProtocolService).sendFile(anyObject(), anyObject());
        PushFileRouteConsumer consumer = new PushFileRouteConsumer(svdTransportProtocolService, mock(SvdTransportProtocolSendParams.class) );
        consumer.setAccount(Account.builder()
                .login("login")
                .password("password")
                .receiveDir("./data/testReceiveDir")
                .requestInterval(5000)
                .sendDir("./data/testSendDir")
                .serverId("1")
                .build());
        PushFileRoute pushFileRoute = new PushFileRoute(mock(OperationLogService.class), consumer);
        pushFileRoute.setFromEndpoint("direct:from");
        return pushFileRoute;
    }

    @Test
    @DirtiesContext
    public void testGetFiles() throws InterruptedException, IOException {
        File file = Files.createTempFile("test", "test").toFile();
        FileUtils.writeStringToFile(file,"<Test>Hello</Test>", Charset.forName("UTF-8") );
        endpoint.sendBody("direct:from", file);

        assertMockEndpointsSatisfied();

        verify(svdTransportProtocolService, times(1)).sendFile(any(), any());
    }
}