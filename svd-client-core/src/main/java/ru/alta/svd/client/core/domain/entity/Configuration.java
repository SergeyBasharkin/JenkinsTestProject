package ru.alta.svd.client.core.domain.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import ru.alta.svd.client.core.domain.entity.types.EncryptionType;
import ru.alta.svd.client.core.domain.entity.types.ServerMode;
import ru.alta.svd.client.core.validator.ConfigurationErrors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "accounts")
@ToString(exclude = "accounts")
public class Configuration {

    private String url;

    private String serverUsername;

    @Builder.Default
    private Integer connectionTimeout = 10;

    @Builder.Default
    private Integer socketTimeout = 10;

    private EncryptionType encryptionType;

    private ServerMode serverMode;

    private String pushServerUrl;

    @Builder.Default
    private Integer operationLogDays = 0;

    private ProxyServerConfig proxy;

    @Builder.Default
    @JacksonXmlElementWrapper(localName = "accounts")
    @JacksonXmlProperty(localName = "account")
    private List<Account> accounts = new ArrayList<>();

    public static boolean configurationIsSet(Configuration configuration) {
        return configuration != null &&
                configuration.getUrl() != null &&
                configuration.getServerUsername() != null &&
                configuration.getConnectionTimeout() != null &&
                configuration.getSocketTimeout() != null &&
                configuration.getOperationLogDays() != null;
    }

    public static Integer validate(Configuration configuration){
        Pattern urlPattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

        if (configuration.getUrl() == null || configuration.getUrl().isEmpty())
            return ConfigurationErrors.INCORRECT_URL;

        if (!urlPattern.matcher(configuration.getUrl()).matches())
            return ConfigurationErrors.INCORRECT_URL;

        if (configuration.getServerUsername() == null || configuration.getServerUsername().isEmpty())
            return ConfigurationErrors.INCORRECT_LOGIN;

        if (configuration.getProxy() != null && configuration.getProxy().isActive()) {
            if (configuration.getProxy().getUrl() == null ||
                    configuration.getProxy().getUrl().isEmpty() ||
                    !urlPattern.matcher(configuration.getProxy().getUrl()).matches())
                return ConfigurationErrors.INCORRECT_PROXY_URL;

            if (configuration.getProxy().getPort() == null || configuration.getProxy().getPort() == 0)
                return ConfigurationErrors.INCORRECT_PROXY_PORT;
        }

        if (configuration.getServerMode() !=null && configuration.getServerMode().equals(ServerMode.PUSH)) {
            if (configuration.getPushServerUrl() == null ||
                    configuration.getPushServerUrl().isEmpty() ||
                    !urlPattern.matcher(configuration.getPushServerUrl()).matches()) {
                return ConfigurationErrors.INVALID_PUSH_SERVER_URL;
            }
        }

        return ConfigurationErrors.OK;
    }
}
