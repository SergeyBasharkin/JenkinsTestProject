package ru.alta.svd.client.core.service.api;

import ru.alta.svd.client.core.domain.entity.Configuration;

import java.io.File;
import java.io.IOException;

public interface ConfigurationService {
    File saveToFile(Configuration configuration) throws IOException;

    Configuration loadConfig();

    Configuration saveConfiguration(Configuration configuration);

    void saveFromFile(File file) throws IOException;

    void destroy();

    File loadFile() throws IOException;

    Configuration parseConfig(File file);
}

