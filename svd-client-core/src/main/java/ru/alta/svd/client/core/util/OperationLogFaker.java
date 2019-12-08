package ru.alta.svd.client.core.util;

import com.github.javafaker.Faker;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.domain.entity.types.OperationType;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

public class OperationLogFaker {
    private final static Faker faker = new Faker(new Locale("ru"));

    public static OperationLog get() {
        return OperationLog.builder()
                .date(LocalDateTime.now())
                .type(randomType())
                .login(faker.name().firstName())
                .fileName(faker.file().fileName())
                .fileSize(faker.number().randomNumber())
                .period(faker.number().randomNumber())
                .resultState(faker.number().numberBetween(0, 38))
                .resultMessage(faker.lorem().sentence())
                .exceptionMessage(faker.lorem().paragraph())
                .build();
    }

    private static OperationType randomType() {
        double chose = new Random().nextInt(3);
        if (chose == 0) return OperationType.SEND;
        if (chose == 1) return OperationType.DELETE;
        if (chose == 2) return OperationType.RECEIVE;
        return OperationType.SEND;
    }
}
