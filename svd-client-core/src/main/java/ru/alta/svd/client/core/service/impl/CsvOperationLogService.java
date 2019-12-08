package ru.alta.svd.client.core.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.alta.svd.client.core.dao.repository.OperationLogRepository;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.domain.entity.types.OperationType;
import ru.alta.svd.client.core.dto.OperationFilter;
import ru.alta.svd.client.core.dto.Page;
import ru.alta.svd.client.core.dto.PageRequest;
import ru.alta.svd.client.core.service.api.OperationLogService;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Qualifier("csvLog")
@Primary
public class CsvOperationLogService implements OperationLogService {

    private final OperationLogRepository repository;

    public CsvOperationLogService(OperationLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void log(OperationLog operationLog) {
        repository.save(operationLog);
    }

    @Override
    public Page<OperationLog> findBy(OperationFilter filter, PageRequest pageRequest) {
        return repository.findBy(filter, pageRequest);
    }

    @Override
    public OperationLog findLastErrorByLogin(String login) {
        return repository.findLastErrorByLogin(login);
    }

    @Override
    public void clear() {
        repository.clear();
    }

    @Override
    public void clearFired(LocalDateTime dateTime) {
       repository.clearFired(dateTime);
    }

    @Override
    public void clearByLogin(String login) {
       repository.clearByLogin(login);
    }
}
