package ru.alta.svd.client.core.dto;

import lombok.Data;
import lombok.NonNull;
import ru.alta.svd.client.core.domain.entity.types.OperationType;

import java.time.temporal.Temporal;
import java.util.List;

/**
 * @author pechenko
 */
@Data
public class OperationFilter {
    private String login;
    private LogType logType;

    public enum LogType {
        SEND, RECEIVE
    }
}
