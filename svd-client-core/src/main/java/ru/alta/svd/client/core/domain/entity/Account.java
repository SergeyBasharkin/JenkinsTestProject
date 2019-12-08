package ru.alta.svd.client.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolSendParams;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "configuration")
@EqualsAndHashCode(exclude = "configuration")
public class Account {

    private Integer id;

    private String login;
    private String password;
    private String serverId;
    private String sendDir;
    private String receiveDir;

    @Builder.Default
    private Integer requestInterval = 10000;

    private SvdTransportProtocolSendParams.MsgTypeEnum msgType;

    @JsonIgnore
    private Configuration configuration;

    @JsonIgnore
    public boolean isValid() {
        return login != null && !login.isEmpty() &&
                password != null && !password.isEmpty() &&
                sendDir != null && !sendDir.isEmpty() &&
                receiveDir != null && !receiveDir.isEmpty();
    }
}
