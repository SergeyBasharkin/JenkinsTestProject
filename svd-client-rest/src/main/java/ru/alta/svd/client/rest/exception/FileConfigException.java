package ru.alta.svd.client.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "file not found")
public class FileConfigException extends RuntimeException {
    public FileConfigException(Throwable cause) {
        super(cause);
    }

}
