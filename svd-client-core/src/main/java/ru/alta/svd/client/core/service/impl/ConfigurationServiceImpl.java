package ru.alta.svd.client.core.service.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.dao.repository.ConfigurationRepository;
import ru.alta.svd.client.core.domain.entity.ProxyServerConfig;
import ru.alta.svd.client.core.listeners.events.ConfigurationEvent;
import ru.alta.svd.client.core.service.api.ConfigurationService;

import java.io.File;
import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RoutesServiceImpl routesService;

    @Override
    public Configuration saveConfiguration(Configuration configuration) {
        Configuration dbConfiguration = loadConfig();

        if (dbConfiguration == null) {
            dbConfiguration = new Configuration();
            dbConfiguration.setProxy(new ProxyServerConfig());
            dbConfiguration.getProxy().setActive(false);
        }

        dbConfiguration.setUrl(configuration.getUrl());
        dbConfiguration.setConnectionTimeout(configuration.getConnectionTimeout());
        dbConfiguration.setEncryptionType(configuration.getEncryptionType());
        dbConfiguration.setOperationLogDays(configuration.getOperationLogDays());
        dbConfiguration.setServerUsername(configuration.getServerUsername());
        dbConfiguration.setSocketTimeout(configuration.getSocketTimeout());

        if (configuration.getProxy() != null && !configuration.getProxy().isActive() && dbConfiguration.getProxy() != null) {
            dbConfiguration.getProxy().setActive(configuration.getProxy().isActive());
        } else {
            dbConfiguration.setProxy(configuration.getProxy());
        }

        boolean serverModeChanged = configuration.getServerMode() != null && !configuration.getServerMode().equals(dbConfiguration.getServerMode());
        boolean pushServerUrlChanged = configuration.getPushServerUrl() != null && !configuration.getPushServerUrl().equals(dbConfiguration.getPushServerUrl());
        if (dbConfiguration.getAccounts() != null && (serverModeChanged || pushServerUrlChanged)) {
            dbConfiguration.getAccounts().forEach(account -> {
                try {
                    routesService.stopOldRoutes(account);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            });
        }

        dbConfiguration.setPushServerUrl(configuration.getPushServerUrl());
        dbConfiguration.setServerMode(configuration.getServerMode());
        return save(dbConfiguration);
    }

    private Configuration save(Configuration configuration) {
        if (configuration.getAccounts() != null)
            configuration.getAccounts().forEach(account -> account.setConfiguration(configuration));
        configuration.getProxy().setConfiguration(configuration);
        Configuration config = configurationRepository.save(configuration);
        applicationEventPublisher.publishEvent(new ConfigurationEvent(this, config));
        return config;
    }

    @Override
    public void saveFromFile(File file) throws IOException {
        Configuration oldConfig = configurationRepository.get();
        if (oldConfig != null) {
            oldConfig.getAccounts().forEach(account -> {
                try {
                    routesService.stopOldRoutes(account);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            });
        }
        Configuration configuration = configurationRepository.recognizeConfig(file);

        if (configuration != null) {
            configurationRepository.saveFromFile(configuration);
            applicationEventPublisher.publishEvent(new ConfigurationEvent(this, configuration));
        }
    }

    @Override
    public void destroy() {
        configurationRepository.destroy();
    }

    @Override
    public File loadFile() throws IOException {
        Configuration configuration = configurationRepository.getRaw();
        XmlMapper xmlMapper = new XmlMapper();
        File file = File.createTempFile("config", ".xml");
        xmlMapper.writeValue(file, configuration);
        return file;
    }

    @Override
    public Configuration parseConfig(File file) {
        return configurationRepository.recognizeConfig(file);
    }

    @Override
    public File saveToFile(Configuration configuration) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        File file = File.createTempFile("config", ".xml");
        xmlMapper.writeValue(file, configuration);
        return file;
    }

    @Override
    public Configuration loadConfig() {
        return configurationRepository.get();
    }
}
