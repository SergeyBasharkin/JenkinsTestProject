package ru.alta.svd.client.core.dao.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.dao.repository.ConfigurationRepository;
import ru.alta.svd.client.core.domain.entity.ProxyServerConfig;
import ru.alta.svd.client.core.domain.entity.types.AuthType;
import ru.alta.svd.client.core.domain.entity.types.EncryptionType;
import ru.alta.svd.client.core.domain.entity.types.ServerMode;
import ru.alta.svd.client.core.service.api.EncryptorService;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolSendParams;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FileConfigurationRepository implements ConfigurationRepository {

    @Value("${svd.client.db:./data/configuration.xml}")
    private String path;
    private XmlMapper mapper = new XmlMapper();
    private final EncryptorService encryptorService;

    public FileConfigurationRepository(EncryptorService encryptorService) {
        this.encryptorService = encryptorService;
    }

    @Override
    public Configuration get() {
        Configuration result = recognizeConfig(Paths.get(path).toFile());
        decrypt(result);
        return result;
    }

    @Override
    public synchronized Configuration save(Configuration configuration) {
        File file = Paths.get(path).toFile();
        File temp = new File(file.getParentFile().getAbsolutePath() + File.separator + "temp_conf.xml");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } else {
                temp.createNewFile();
            }

            encrypt(configuration);

            mapper.writeValue(temp, configuration);

            file.delete();
            temp.renameTo(file);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return configuration;
    }

    @Override
    public void destroy() {
        Paths.get(path).toFile().delete();
    }


    public Configuration convert(OldConfiguration oldConfiguration) {
        Configuration configuration = Configuration.builder().proxy(ProxyServerConfig.builder().build()).build();
        if (oldConfiguration != null) {
            BeanUtils.copyProperties(oldConfiguration, configuration);
            if (oldConfiguration.getProxy() != null) {
                BeanUtils.copyProperties(oldConfiguration.getProxy(), configuration.getProxy());
                configuration.getProxy().setLogin(oldConfiguration.getProxy().getUser());
                configuration.getProxy().setUrl(oldConfiguration.getProxy().getHost());

                if (oldConfiguration.getProxy().getAuthScheme()!= null && oldConfiguration.getProxy().getAuthScheme().toUpperCase().equals("BASIC"))
                    configuration.getProxy().setAuthType(AuthType.Basic);

                if (oldConfiguration.getProxy().getAuthScheme()!= null && oldConfiguration.getProxy().getAuthScheme().toUpperCase().equals("NTLM"))
                    configuration.getProxy().setAuthType(AuthType.NTLM);

                if (oldConfiguration.getAccounts() != null && !oldConfiguration.getAccounts().isEmpty()) {
                    AtomicInteger id = new AtomicInteger();
                    configuration.setAccounts(oldConfiguration.getAccounts()
                            .stream().map(oldAccount -> {
                                Account account = Account.builder().build();
                                BeanUtils.copyProperties(oldAccount, account);
                                account.setId(id.getAndIncrement());
                                account.setRequestInterval(oldAccount.getRequestInteval());
                                account.setMsgType(oldAccount.getMsgType() != null && oldAccount.getMsgType().equals("ED") ? SvdTransportProtocolSendParams.MsgTypeEnum.ED : null);
                                return account;
                            }).collect(Collectors.toList())
                    );
                }
            }
        }
        return configuration;
    }

    public Configuration convert(OldConfiguration_2 oldConfiguration) {
        Configuration configuration = Configuration.builder().proxy(ProxyServerConfig.builder().build()).build();
        if (oldConfiguration != null) {
            BeanUtils.copyProperties(oldConfiguration, configuration);
            if (oldConfiguration.getEncryptionEnabled() != null && oldConfiguration.getEncryptionEnabled()) {
                configuration.setEncryptionType(EncryptionType.RSA);
            } else {
                configuration.setEncryptionType(null);
            }
            if (oldConfiguration.getProxy() != null) {
                BeanUtils.copyProperties(oldConfiguration.getProxy(), configuration.getProxy());
                configuration.getProxy().setLogin(oldConfiguration.getProxy().getUser());
                configuration.getProxy().setUrl(oldConfiguration.getProxy().getHost());

                if (oldConfiguration.getProxy().getAuthScheme()!= null && oldConfiguration.getProxy().getAuthScheme().toUpperCase().equals("BASIC"))
                    configuration.getProxy().setAuthType(AuthType.Basic);

                if (oldConfiguration.getProxy().getAuthScheme()!= null && oldConfiguration.getProxy().getAuthScheme().toUpperCase().equals("NTLM"))
                    configuration.getProxy().setAuthType(AuthType.NTLM);

                if (oldConfiguration.getAccounts() != null && !oldConfiguration.getAccounts().isEmpty()) {
                    AtomicInteger id = new AtomicInteger();
                    configuration.setAccounts(oldConfiguration.getAccounts()
                            .stream().map(oldAccount -> {
                                Account account = Account.builder().build();
                                BeanUtils.copyProperties(oldAccount, account);
                                account.setId(id.getAndIncrement());
                                account.setRequestInterval(oldAccount.getReceiveSleep());
                                account.setMsgType(oldAccount.getMsgType() != null && oldAccount.getMsgType().equals("ED") ? SvdTransportProtocolSendParams.MsgTypeEnum.ED : null);
                                return account;
                            }).collect(Collectors.toList())
                    );
                }
            }
        }
        return configuration;
    }

    public Configuration recognizeConfig(File file){
        Configuration result = null;
        try {
            Configuration configuration = mapper.readValue(file, Configuration.class);
            configuration.setAccounts(configuration.getAccounts() == null ? new ArrayList<>() : configuration.getAccounts());
            result = configuration;
        } catch (IOException e) {
            try {
                OldConfiguration oldConfiguration = mapper.readValue(file, OldConfiguration.class);
                result = convert(oldConfiguration);
            } catch (IOException e1) {
                try {
                    OldConfiguration_2 oldConfiguration_2 = mapper.readValue(file, OldConfiguration_2.class);
                    result = convert(oldConfiguration_2);
                } catch (IOException e2) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }

    @Override
    public Configuration saveFromFile(Configuration configuration) {
        File file = Paths.get(path).toFile();
        File temp = new File(file.getParentFile().getAbsolutePath() + File.separator + "temp_conf.xml");

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }else{
                temp.createNewFile();
            }

            mapper.writeValue(temp, configuration);

            file.delete();
            temp.renameTo(file);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return configuration;
    }

    @Override
    public Configuration getRaw() {
        return recognizeConfig(Paths.get(path).toFile());
    }

    private void decrypt(Configuration result){
        if (result != null) {
            if (result.getProxy() != null && result.getProxy().getPassword() != null)
                result.getProxy().setPassword(new String(encryptorService.decrypt(result.getProxy().getPassword())));

            if (result.getAccounts() != null) {
                result.getAccounts().stream().filter(account -> account.getPassword() != null).forEach(account -> {
                    account.setPassword(new String(encryptorService.decrypt(account.getPassword())));
                });
            }
        }
    }

    private void encrypt(Configuration configuration){
        if (configuration.getAccounts() != null) {
            configuration.getAccounts().stream()
                    .filter(account -> account.getPassword() != null)
                    .forEach(account -> account.setPassword(new String(Base64.encodeBase64(encryptorService.encrypt(account.getPassword())))));
        }
        if (configuration.getProxy() != null && configuration.getProxy().getPassword() != null)
            configuration.getProxy().setPassword(new String(Base64.encodeBase64(encryptorService.encrypt(configuration.getProxy().getPassword()))));
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OldConfiguration_2 {
        private Boolean encryptionEnabled;
        private String url;
        private String serverUsername;
        private Integer connectionTimeout;
        private Integer socketTimeout;

        private EncryptionType encryptionType;
        private ServerMode serverMode;
        private String pushServerUrl;

        private OldProxy_2 proxy;
        private List<OldAccount_2> accounts = new ArrayList<>();

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OldProxy_2 {
            private Boolean active;
            private String host;
            private Integer port;
            private String user;
            private String password;
            private String authScheme;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OldAccount_2 {
            private String login;
            private String password;
            private String serverId;
            private String sendDir;
            private String receiveDir;
            private Integer receiveSleep;

            private String msgType;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OldConfiguration {
        private String url;
        private String serverUsername;
        private Integer connectionTimeout;
        private Integer socketTimeout;
        private EncryptionType encryptionType;

        private ServerMode serverMode;
        private String pushServerUrl;

        private OldProxy proxy;
        private List<OldAccount> accounts = new ArrayList<>();

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OldProxy {
            private Boolean active;
            private Integer port;
            private String password;
            private String authScheme;

            private String user;
            private String host;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OldAccount {
            private String login;
            private String password;
            private String serverId;
            private String sendDir;
            private String receiveDir;
            private Integer requestInteval;

            private String msgType;
        }
    }
}
