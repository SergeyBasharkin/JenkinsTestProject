package ru.alta.svd.client.core.service.api;


import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.dto.OperationFilter;
import ru.alta.svd.client.core.dto.Page;
import ru.alta.svd.client.core.dto.PageRequest;

import java.time.LocalDateTime;

public interface OperationLogService {
    void log(OperationLog operationLog);

    Page<OperationLog> findBy(OperationFilter filter, PageRequest pageRequest);

    OperationLog findLastErrorByLogin(String login);

    void clear();

    void clearFired(LocalDateTime dateTime);

    void clearByLogin(String login);
}
