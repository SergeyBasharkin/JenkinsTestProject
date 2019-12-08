package ru.alta.svd.client.core.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.core.dao.repository.OperationLogRepository;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.domain.entity.types.OperationType;
import ru.alta.svd.client.core.dto.OperationFilter;
import ru.alta.svd.client.core.dto.Page;
import ru.alta.svd.client.core.dto.PageRequest;
import ru.alta.svd.client.core.validator.SVDErrors;

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

@Component
@Slf4j
public class CsvOperationLogRepository implements OperationLogRepository {

    @Override
    public void save(OperationLog operationLog) {
        if (operationLog.getLogin() == null || operationLog.getType() == null){
            log.error("operationLog null");
            return;
        }
        String filePath = recognizeFilePath(operationLog.getLogin(), getLogType(operationLog.getType()).toString());

        File file = Paths.get(filePath).toFile();
        if (!file.getParentFile().mkdirs()) {
            OperationLog last = findOne(file);
            if (last != null && !last.getDate().toLocalDate().equals(operationLog.getDate().toLocalDate())) {
                toArchive(operationLog);
            }
        }

        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            fileWriter.write(operationLog.toCsv() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Page<OperationLog> findBy(OperationFilter filter, PageRequest pageRequest) {
        Page<OperationLog> page = new Page<>();
        File file = Paths.get(recognizeFilePath(filter.getLogin(), filter.getLogType().toString())).toFile();

        if (!file.exists()) return page;
        List<OperationLog> operationLogs = new LinkedList<>();

        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, Charset.forName("UTF-8"))) {
            int offset = offset(reader, pageRequest.getOffset());

            boolean endInFor = false;
            for (int i = 0; i < pageRequest.getSize(); i++) {
                OperationLog operationLog = null;
                try {
                    operationLog = OperationLog.fromCsv(reader.readLine());
                } catch (NullPointerException ignored) { }

                if (operationLog == null) {
                    endInFor = true;
                    break;
                }
                operationLogs.add(operationLog);
            }

            page.setLast(endInFor);
            page.setContent(operationLogs);

            return page;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return page;
    }

    @Override
    public OperationLog findLastErrorByLogin(String login) {
        File receiveFile = Paths.get(recognizeFilePath(login, OperationFilter.LogType.RECEIVE.toString())).toFile();
        File sendFile = Paths.get(recognizeFilePath(login, OperationFilter.LogType.SEND.toString())).toFile();

        OperationLog receiveLog = null;
        OperationLog sendLog = null;
        if (!receiveFile.exists() && !sendFile.exists()) return null;
        if (receiveFile.exists()) receiveLog = findOne(receiveFile);
        if (sendFile.exists()) sendLog = findOne(sendFile);

        if (receiveLog == null && sendLog == null) return null;
        if (receiveLog != null && sendLog != null && receiveLog.getDate() != null && sendLog.getDate() != null) {
            if (receiveLog.getDate().isAfter(sendLog.getDate())) {
                return receiveValid(receiveLog.getResultState()) ? null : receiveLog;
            } else {
                return sendValid(sendLog.getResultState()) ? null : sendLog;
            }
        }
        if (receiveLog == null && !sendValid(sendLog.getResultState())) return sendLog;
        if (sendLog == null && !receiveValid(receiveLog.getResultState())) return receiveLog;

        return null;
    }

    private boolean sendValid(Integer result) {
        return result.equals(SVDErrors.SENT_SUCCESSFULLY);
    }

    private boolean receiveValid(Integer result) {
        return result.equals(SVDErrors.ACCEPTED_SUCCESSFULLY) || result.equals(SVDErrors.DELETED_SUCCESSFULLY);

    }

    @Override
    public void clear() {
        try {
            FileUtils.deleteDirectory(Paths.get("./logs/").toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearFired(LocalDateTime dateTime) {
        Path root = Paths.get("./logs");
        try {
            Files.newDirectoryStream(root).forEach(path -> {
                if (path.toFile().isDirectory()) {
                    try {
                        Files.newDirectoryStream(path,
                                entry -> entry.toFile().isFile() &&
                                        entry.toFile().getName().split("\\.")[1].equals("zip") &&
                                        LocalDate.parse(entry.toFile().getName().split("_")[0]).isBefore(dateTime.toLocalDate())
                        ).forEach(file -> file.toFile().delete());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearByLogin(String login) {
        File dir = Paths.get(getDirPath(login)).toFile();
        if (dir.listFiles() != null) Stream.of(Objects.requireNonNull(dir.listFiles())).forEach(File::delete);
    }

    private String recognizeFilePath(String login, String type) {
        return getDirPath(login) + type + ".csv";
    }

    private void toArchive(OperationLog operationLog) {
        String zipPath = getDirPath(operationLog.getLogin()) + operationLog.getDate().toLocalDate().toString() + "_" + getLogType(operationLog.getType()) + ".zip";
        String filePath = recognizeFilePath(operationLog.getLogin(), getLogType(operationLog.getType()).toString());
        File file = Paths.get(filePath).toFile();
        try (
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(Paths.get(zipPath).toFile()));
                FileInputStream fis = new FileInputStream(file)
        ) {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.delete();
    }

    private OperationLog findOne(File file) {
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, Charset.forName("UTF-8"))) {
            return OperationLog.fromCsv(reader.readLine());
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }


    private int offset(ReversedLinesFileReader reader, int offset) throws IOException {
        int i = 0;
        while (offset > i) {
            i++;
            if (reader.readLine() == null) break;
        }
        return i;
    }

    private OperationFilter.LogType getLogType(OperationType type) {
        return (type.equals(OperationType.DELETE) || type.equals(OperationType.RECEIVE)) ? OperationFilter.LogType.RECEIVE : OperationFilter.LogType.SEND;
    }

    private String getDirPath(String login) {
        String path = "./logs";
        return path + File.separator + login + File.separator;
    }
}
