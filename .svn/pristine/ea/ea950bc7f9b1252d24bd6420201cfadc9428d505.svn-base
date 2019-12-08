package ru.alta.svd.client.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alta.svd.client.core.domain.entity.types.AuthType;
import ru.alta.svd.client.core.domain.entity.types.EncryptionType;
import ru.alta.svd.client.core.domain.entity.types.ServerMode;

/**
 *
 * @author pechenko
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigObject {
    private Long id;

    private String url;

    private String serverUsername;

    private Integer connectionTimeout;

    private Integer socketTimeout;

    private EncryptionType encryptionType;

    private ServerMode serverMode;

    private String pushServerUrl;

    private Integer operationLogDays;

    private ProxyServerConfigObject proxy = new ProxyServerConfigObject();

    private Integer validationError;


    @Data
    public static class ProxyServerConfigObject{
        private Long id;

        private boolean active;
        private String url;
        private Integer port;
        private String login;
        private String password;

        private AuthType authType;
    }
}
