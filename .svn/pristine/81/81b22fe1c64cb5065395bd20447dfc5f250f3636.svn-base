package ru.alta.svd.client.core.dao.repository;

import org.springframework.stereotype.Repository;
import ru.alta.svd.client.core.domain.entity.Configuration;

import java.io.File;

public interface ConfigurationRepository {

    Configuration get();

    Configuration save(Configuration configuration);

    void destroy();

    Configuration recognizeConfig(File file);

    Configuration saveFromFile(Configuration configuration);

    Configuration getRaw();
}
