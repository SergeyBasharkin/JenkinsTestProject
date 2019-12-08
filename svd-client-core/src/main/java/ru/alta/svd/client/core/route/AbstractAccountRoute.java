package ru.alta.svd.client.core.route;

import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.transport.protocol.util.OperationLogUtil;

@AllArgsConstructor
abstract class AbstractAccountRoute extends RouteBuilder {

    @Qualifier("csvLog")
    protected final OperationLogService operationLogService;

    @Override
    public void configure() throws Exception {
        onException(Exception.class).handled(true).process(exchange -> {
            Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
            this.operationLogService.log(OperationLog.getDefaultBuilder(exchange)
                    .exceptionMessage(ExceptionUtils.getStackTrace(cause))
                    .period(OperationLogUtil.getElapsed(exchange))
                    .build());
            throw cause;
        }).end();

        configureInternal();
    }

    protected abstract void configureInternal() throws Exception;
}
