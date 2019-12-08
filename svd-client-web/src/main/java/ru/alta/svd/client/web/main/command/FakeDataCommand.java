package ru.alta.svd.client.web.main.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.client.core.util.OperationLogFaker;

@Component
public class FakeDataCommand implements CommandLineRunner {

    @Value("${test.generateCount:0}")
    private int generateCount;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public void run(String... args) throws Exception {
        if (generateCount > 0) {
            for (int i = 0; i < generateCount; i++) {
                operationLogService.log(OperationLogFaker.get());
            }
        }
    }
}
