package ru.alta.svd.client.core.service.impl;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolFileInfo;
import ru.alta.svd.transport.protocol.dto.XMLFile;
import ru.alta.svd.transport.protocol.exception.SvdTransportProtocolException;
import ru.alta.svd.transport.protocol.service.api.SvdTransportProtocolResponseHandler;

import java.io.IOException;
import java.io.InputStream;

public class SvdClientResponseHandler implements SvdTransportProtocolResponseHandler {

    private final ProducerTemplate producerTemplate;
    private final Account account;

    public SvdClientResponseHandler(ProducerTemplate producerTemplate, Account account) {
        this.producerTemplate = producerTemplate;
        this.account = account;
    }

    @Override
    public void process(SvdTransportProtocolFileInfo svdTransportProtocolFileInfo, InputStream inputStream) throws SvdTransportProtocolException {
        XMLFile.File file = new XMLFile.File();

        file.setId(svdTransportProtocolFileInfo.getId() == null ?
                "" : String.valueOf(svdTransportProtocolFileInfo.getId()));

        file.setStatId(svdTransportProtocolFileInfo.getStatId() == null ?
                "" : String.valueOf(svdTransportProtocolFileInfo.getStatId()));

        file.setLogin(svdTransportProtocolFileInfo.getLogin() == null ?
                "" : svdTransportProtocolFileInfo.getLogin());
        try {
            file.setContent(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new SvdTransportProtocolException(e);
        }
        try {
            producerTemplate.sendBody(account == null ? "{{svd.getDoc.fromEndpoint}}" : "direct:getFileTo" + account.getLogin(), file);
        }catch (Exception e){
            throw new SvdTransportProtocolException(e);
        }
    }
}
