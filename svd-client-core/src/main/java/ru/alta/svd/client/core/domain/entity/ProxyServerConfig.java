package ru.alta.svd.client.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.alta.svd.client.core.domain.entity.types.AuthType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "configuration")
@EqualsAndHashCode(exclude = "configuration")
public class ProxyServerConfig {

    private boolean active;
    private String url;
    private Integer port;
    private String login;
    private String password;

    private AuthType authType;

    @JsonIgnore
    private Configuration configuration;
}
