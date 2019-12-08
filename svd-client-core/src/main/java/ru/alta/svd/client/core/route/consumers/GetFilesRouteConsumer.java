package ru.alta.svd.client.core.route.consumers;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Exchange;
import org.apache.camel.RecipientList;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.route.types.SVDHeader;
import ru.alta.svd.transport.protocol.dto.XMLFile;

import java.io.File;
import java.io.FileNotFoundException;

public class GetFilesRouteConsumer {

    @Getter
    @Setter
    private Account account;

    @Setter
    private String saveFileEndpoint = "{{svd.getDoc.saveFile}}";

    public GetFilesRouteConsumer(Account account) {
        this.account = account;
    }

    public void xmlToFile(XMLFile.File file, Exchange exchange) {
        exchange.getIn().setHeader(SVDHeader.SVD_RECEIVE_DIR, getAccount().getReceiveDir());
        exchange.getIn().setHeader("CamelFileName", file.getId() + ".xml");
        exchange.getIn().setHeader("CamelFileLength", (long) file.getContent().length);
        exchange.getIn().setBody(file.getContent());
    }

    public void checkFiles(Exchange exchange) throws Exception {
        File file = new File(exchange.getIn().getHeader(SVDHeader.SVD_RECEIVE_DIR, String.class) + "/" + exchange.getIn().getHeader("CamelFileName"));
        if (!file.exists()) throw new FileNotFoundException("file with name " +exchange.getIn().getHeader("CamelFileName") + " Not found");
    }

    @RecipientList
    public String recognizeSaveFileEndpoint(){
        return saveFileEndpoint;
    }
}
