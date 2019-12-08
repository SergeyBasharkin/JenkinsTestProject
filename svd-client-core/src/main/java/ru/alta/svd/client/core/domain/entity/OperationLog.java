package ru.alta.svd.client.core.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import ru.alta.svd.client.core.domain.entity.types.OperationType;
import ru.alta.svd.client.core.dto.XMLFile;
import ru.alta.svd.client.core.jackson.deserializer.LocalDateTimeDeserializer;
import ru.alta.svd.client.core.jackson.serializer.LocalDateTimeSerializer;
import ru.alta.svd.client.core.route.types.SVDHeader;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Slf4j
public class OperationLog {
    private Long id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;

    private OperationType type;

    private String login;

    private String fileName;

    private Long fileSize;

    private Long period;

    private Integer resultState;

    private String resultMessage;

    private String exceptionMessage;

    public static OperationLog.OperationLogBuilder getDefaultBuilder(Exchange exchange) {
        XMLFile.OperationResult result = exchange.getIn().getHeader("OperationResult", XMLFile.OperationResult.class);
        return OperationLog.builder()
                .date(LocalDateTime.now())
                .login((String) exchange.getIn().getHeader(SVDHeader.SVD_LOGIN))
                .type((OperationType) exchange.getIn().getHeader(SVDHeader.SVD_ROUTE_TYPE))
                .fileName((String) exchange.getIn().getHeader("CamelFileName"))
                .fileSize((Long) exchange.getIn().getHeader("CamelFileLength"))
                .period(0L)
                .resultState(result == null ? -1 : result.getState())
                .resultMessage(result == null ? "" : result.getMessage());
    }

    public static OperationLog fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] csv = line.trim().split(";");
        if (csv.length != 9) return OperationLog.builder().date(LocalDateTime.now()).exceptionMessage("Не удалось считать лог").build();
        return OperationLog.builder()
                .date((csv[0] == null || csv[0].equals("null")) ? LocalDateTime.now() : LocalDateTime.parse(csv[0]))
                .type((csv[1] == null || csv[1].equals("null")) ? OperationType.UNKNOWN : OperationType.valueOf(csv[1]))
                .login((csv[2] == null || csv[2].equals("null") ? "" : csv[2]))
                .fileName((csv[3] == null || csv[3].equals("null") ? "" : csv[3]))
                .fileSize(Long.valueOf((csv[4] == null || csv[4].equals("null")) ? "0" : csv[4]))
                .period(Long.valueOf((csv[5] == null || csv[5].equals("null")) ? "0" : csv[5]))
                .resultState(Integer.valueOf((csv[6] == null || csv[6].equals("null")) ? "0" : csv[6]))
                .resultMessage((csv[7] == null || csv[7].equals("null") ? "" : csv[7]))
                .exceptionMessage((csv[8] == null || csv[8].equals("null") ? "" : csv[8]))
                .build();
    }

    public String toCsv() {
        return date + ";" +
                type + ";" +
                login + ";" +
                fileName + ";" +
                fileSize + ";" +
                period + ";" +
                resultState + ";" +
                formatExceptionMessage(resultMessage) + ";" +
                formatExceptionMessage(exceptionMessage);
    }

    @Override
    public String toString() {
        return "OperationLog{" +
                "id=" + id +
                ", date=" + date +
                ", type=" + type +
                ", login='" + login + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", period=" + period +
                ", resultState=" + resultState +
                ", resultMessage='" + resultMessage + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                '}';
    }

    private String formatExceptionMessage(String exceptionMessage) {
        if (exceptionMessage == null || exceptionMessage.isEmpty()) return "null";
        exceptionMessage = exceptionMessage.replaceAll("\\s+", " ");
        return exceptionMessage;
    }
}
