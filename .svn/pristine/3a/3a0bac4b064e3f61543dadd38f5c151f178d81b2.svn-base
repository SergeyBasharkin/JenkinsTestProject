package ru.alta.svd.client.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolSendParams;

/**
 *
 * @author pechenko
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AccountObject {
    private Integer id;
    private String login;
    private String password;
    private String serverId;
    private String sendDir;
    private String receiveDir;
    private Integer requestInterval;
    private Integer validationError;
    private Integer transportError;
    private Integer duplicateError;
    private String msgType;
}
