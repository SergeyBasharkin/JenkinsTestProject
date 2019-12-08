package ru.alta.svd.client.core.route.events;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Consume;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.domain.entity.types.OperationType;
import ru.alta.svd.client.core.route.types.SVDHeader;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolNotificationInfo;
import ru.alta.svd.transport.protocol.util.OperationLogUtil;
import ru.alta.svd.transport.protocol.util.SvdProtocolConstants;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ErrorHandler {

    private final OperationLogService operationLogService;

    public ErrorHandler(@Qualifier("csvLog") OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Consume(uri = "direct:dead")
    public void error(Exchange exchange, Exception e) throws Exception {
        SvdTransportProtocolNotificationInfo.OperationType operationType = exchange
                .getIn()
                .getHeader(
                        SVDHeader.SVD_ROUTE_TYPE,
                        SvdTransportProtocolNotificationInfo.OperationType.class
                );
        OperationType type = OperationType.UNKNOWN;
        if (operationType != null) {
            type = OperationType.valueOf(operationType.toString());
        }
        Boolean alreadyLog = exchange.getIn().getHeader(SvdProtocolConstants.STATE_ERRORS, Boolean.class);
        if (type.equals(OperationType.UNKNOWN)) {
            log.error(ExceptionUtils.getStackTrace(e));
        } else if (alreadyLog == null || !alreadyLog){
            Integer resultState = exchange.getIn().getHeader(SvdProtocolConstants.SVD_RESULT_CODE, Integer.class);
            operationLogService.log(OperationLog.builder()
                    .period(OperationLogUtil.getElapsed(exchange))
                    .date(LocalDateTime.now())
                    .exceptionMessage(ExceptionUtils.getStackTrace(e))
                    .login(exchange.getIn().getHeader(SVDHeader.SVD_LOGIN, String.class))
                    .type(type)
                    .fileName(exchange.getIn().getHeader(SvdProtocolConstants.FILE_NAME, String.class))
                    .fileSize(exchange.getIn().getHeader(SvdProtocolConstants.FILE_LENGTH, Long.class))
                    .resultState(resultState == null ? 0 : resultState)
                    .build()
            );
        }
    }
}
