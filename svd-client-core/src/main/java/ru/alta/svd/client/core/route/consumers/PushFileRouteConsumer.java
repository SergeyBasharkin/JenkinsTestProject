package ru.alta.svd.client.core.route.consumers;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.types.OperationType;
import ru.alta.svd.client.core.route.types.SVDHeader;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolSendParams;
import ru.alta.svd.transport.protocol.service.api.SvdTransportProtocolService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static ru.alta.svd.transport.protocol.util.SvdProtocolConstants.FILE_LENGTH;
import static ru.alta.svd.transport.protocol.util.SvdProtocolConstants.FILE_NAME;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PushFileRouteConsumer {

    @Getter
    @Setter
    private Account account;

    private final SvdTransportProtocolService svdTransportProtocolService;
    private final SvdTransportProtocolSendParams svdTransportProtocolSendParams;
    @Setter
    private String serverUsername;

    public PushFileRouteConsumer(SvdTransportProtocolService svdTransportProtocolService, SvdTransportProtocolSendParams svdTransportProtocolSendParams) {
        this.svdTransportProtocolService = svdTransportProtocolService;
        this.svdTransportProtocolSendParams = svdTransportProtocolSendParams;
    }

    public void sendFile(Exchange exchange) throws FileNotFoundException, UnknownHostException {
        File file = exchange.getIn().getBody(File.class);
        exchange.getIn().setHeader(SVDHeader.SVD_LOGIN, account.getLogin());
        exchange.getIn().setHeader(SVDHeader.SVD_ROUTE_TYPE, OperationType.SEND);
        exchange.getIn().setHeader(FILE_NAME, file.getName());
        exchange.getIn().setHeader(FILE_LENGTH, file.length());
        exchange.getIn().setHeader("CamelFileName", file.getName());
        exchange.getIn().setHeader("CamelFileLength", file.length());

        svdTransportProtocolSendParams.setLogin(account.getLogin());
        svdTransportProtocolSendParams.setPassword(account.getPassword());
        svdTransportProtocolSendParams.setToLogin(serverUsername);
        svdTransportProtocolSendParams.setMsgType(account.getMsgType());
        svdTransportProtocolSendParams.setFileName(file.getName());
        svdTransportProtocolSendParams.setNoDecode("1");
        svdTransportProtocolSendParams.setCompression("zip");
        svdTransportProtocolSendParams.setCompName(InetAddress.getLocalHost().getHostName());
        svdTransportProtocolSendParams.setServId(account.getServerId());
        svdTransportProtocolSendParams.setMsgType(account.getMsgType());
        svdTransportProtocolSendParams.setClientProgram("SvdClient");
        svdTransportProtocolService.sendFile(
                svdTransportProtocolSendParams,
                new FileInputStream(file)
        );
    }
}
