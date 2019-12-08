package ru.alta.svd.client.core.route.consumers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.core.service.api.ConfigurationService;
import ru.alta.svd.client.core.service.api.OperationLogService;

import java.time.LocalDateTime;

@Component
public class OperationLogConsumer {

    private final OperationLogService operationLogService;
    private final ConfigurationService configurationService;

    public OperationLogConsumer(@Qualifier("csvLog") OperationLogService operationLogService, ConfigurationService configurationService) {
        this.operationLogService = operationLogService;
        this.configurationService = configurationService;
    }

    public void clearOperationLog(){
        Integer days = configurationService.loadConfig().getOperationLogDays();
        if (days > 0) operationLogService.clearFired(LocalDateTime.now().minusDays(days));
    }
}
