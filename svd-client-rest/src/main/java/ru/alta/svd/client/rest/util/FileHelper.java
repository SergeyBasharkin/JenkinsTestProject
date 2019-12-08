package ru.alta.svd.client.rest.util;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class FileHelper {
    public static File multipartToFile(MultipartFile multipart) throws IOException {
        File file = File.createTempFile(UUID.randomUUID().toString(), "temp");
        FileOutputStream o = new FileOutputStream(file);
        IOUtils.copy(multipart.getInputStream(), o);
        o.close();
        return file;
    }
}
