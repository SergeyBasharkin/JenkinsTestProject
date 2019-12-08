package ru.alta.svd.client.core.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alta.svd.client.core.domain.entity.Account;
import ru.alta.svd.client.core.domain.entity.Configuration;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.domain.entity.types.OperationType;
import ru.alta.svd.client.core.domain.entity.types.ServerMode;
import ru.alta.svd.client.core.listeners.events.PushFileRouteStart;
import ru.alta.svd.client.core.route.GetFilesRoute;
import ru.alta.svd.client.core.route.PushFileRoute;
import ru.alta.svd.client.core.route.consumers.GetFilesRouteConsumer;
import ru.alta.svd.client.core.route.consumers.PushFileRouteConsumer;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.client.core.util.SvdProtocolUtil;
import ru.alta.svd.client.core.validator.ConfigurationErrors;
import ru.alta.svd.transport.protocol.camel.consumer.AbstractGetFilesConsumer;
import ru.alta.svd.transport.protocol.camel.consumer.GetFileLongPollConsumer;
import ru.alta.svd.transport.protocol.camel.consumer.SendFileConsumer;
import ru.alta.svd.transport.protocol.camel.route.SendFileRoute;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolGetConfig;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolGetParams;
import ru.alta.svd.transport.protocol.dto.SvdTransportProtocolSendParams;
import ru.alta.svd.transport.protocol.service.api.SvdTransportProtocolService;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RoutesServiceImpl {

    private final SvdTransportProtocolService svdTransportProtocolService;
    private final SvdTransportProtocolSendParams svdTransportProtocolSendParams;
    private final CamelContext camelContext;
    private final ProducerTemplate producerTemplate;
    private final SendFileConsumer sendFileConsumer;
    private final OperationLogService operationLogService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private Map<String, GetFilesRouteConsumer> getFilesRouteConsumerMap = new HashMap<>();
    private Map<String, PushFileRouteConsumer> pushFileRouteConsumerMap = new HashMap<>();

    public RoutesServiceImpl(SvdTransportProtocolService svdTransportProtocolService,
                             SvdTransportProtocolSendParams svdTransportProtocolSendParams,
                             CamelContext camelContext,
                             ProducerTemplate producerTemplate,
                             SendFileConsumer sendFileConsumer,
                             OperationLogService operationLogService,
                             ApplicationEventPublisher applicationEventPublisher) {
        this.svdTransportProtocolService = svdTransportProtocolService;
        this.svdTransportProtocolSendParams = svdTransportProtocolSendParams;
        this.camelContext = camelContext;
        this.producerTemplate = producerTemplate;
        this.sendFileConsumer = sendFileConsumer;
        this.operationLogService = operationLogService;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    public void stopOldRoutes(Account oldAccount) throws Exception {
        if (oldAccount != null) {
            svdTransportProtocolService.unregisterGetFileHandler(
                    SvdTransportProtocolGetParams.builder()
                            .login(oldAccount.getLogin())
                            .build()
            );
            stopRoutes(oldAccount);
        }
    }

    public void startRoutes(List<Account> accounts, Configuration configuration) {

        accounts.forEach(account -> {
            try {

                boolean pushValid = true;

                if (configuration.getServerMode() != null && configuration.getServerMode().equals(ServerMode.PUSH)){
                    boolean confValid = configuration.getPushServerUrl() != null && !configuration.getPushServerUrl().isEmpty() && Configuration.validate(configuration) == ConfigurationErrors.OK;
                    boolean accValid = account.getServerId() != null && !account.getServerId().isEmpty();

                    pushValid = confValid && accValid;
                }

                if (camelContext.getRoute("route-get-file-" + account.getLogin()) == null && !getFilesRouteConsumerMap.containsKey(account.getLogin()) && pushValid)
                    registerGetFilesRoute(account, configuration);

                if (camelContext.getRoute("route-push-" + account.getLogin()) == null && !pushFileRouteConsumerMap.containsKey(account.getLogin()) && pushValid)
                    registerPushFilesRoute(account, configuration);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        configurationRefresh(configuration);
    }

    private void registerGetFilesRoute(Account account, Configuration configuration) throws Exception {
        GetFilesRouteConsumer getFilesRouteConsumer = new GetFilesRouteConsumer(account);
        getFilesRouteConsumer.setSaveFileEndpoint("file:" + (account.getReceiveDir() == null || account.getReceiveDir().isEmpty() ? "/" : account.getReceiveDir() + "?charset=utf-8"));
        getFilesRouteConsumerMap.put(account.getLogin(), getFilesRouteConsumer);

        GetFilesRoute getFilesRoute = new GetFilesRoute(operationLogService, getFilesRouteConsumer);
        svdTransportProtocolService.registerNotificationHandler(
                svdTransportProtocolNotificationInfo -> operationLogService.log(OperationLog.builder()
                        .login(account.getLogin())
                        .date(LocalDateTime.now())
                        .type(OperationType.valueOf(svdTransportProtocolNotificationInfo.getType().toString()))
                        .resultState(svdTransportProtocolNotificationInfo.getState())
                        .resultMessage(svdTransportProtocolNotificationInfo.getMessage())
                        .period(svdTransportProtocolNotificationInfo.getPeriod())
                        .fileSize(svdTransportProtocolNotificationInfo.getFileSize())
                        .fileName(svdTransportProtocolNotificationInfo.getFilename())
                        .exceptionMessage(svdTransportProtocolNotificationInfo.getException())
                        .build()),
                account.getLogin());

        SvdTransportProtocolGetConfig.SvdTransportProtocolGetConfigBuilder confBuilder =  SvdTransportProtocolGetConfig.builder()
                .getFileEndpoint(SvdProtocolUtil.buildUrl(configuration.getUrl()) + "/getFiles.do")
                .deleteFilesEndpoint(SvdProtocolUtil.buildUrl(configuration.getUrl()) + "/deleteFile.do");

        if (configuration.getServerMode() != null && configuration.getServerMode().equals(ServerMode.PUSH)){
            confBuilder.serverMode(configuration.getServerMode().toString());
            String pushUrl = configuration.getPushServerUrl().endsWith("/") ? configuration.getPushServerUrl() : configuration.getPushServerUrl() + "/";
            confBuilder.pushServerUrl(pushUrl + account.getServerId() + "_" + account.getLogin());
        } else {
            confBuilder.fromEndpoint(account.getRequestInterval() != null ? "timer:start?period=" + account.getRequestInterval() : "");
        }

        svdTransportProtocolService.registerGetFileHandler(
                SvdTransportProtocolGetParams.builder()
                        .servId(account.getServerId())
                        .password(account.getPassword())
                        .login(account.getLogin())
                        .compName(InetAddress.getLocalHost().getHostName())
                        .build(),
                new SvdClientResponseHandler(producerTemplate, account),
                confBuilder.build()
        );

//        camelContext.getShutdownStrategy().setShutdownNowOnTimeout(true);

        camelContext.addRoutes(getFilesRoute);
    }

    private void registerPushFilesRoute(Account account, Configuration configuration) throws Exception {
        startPushFilesRoute(account, configuration);

        if (camelContext.getRoute("send-file-route") == null) {
            SendFileRoute sendFileRoute = new SendFileRoute(sendFileConsumer);
            sendFileConsumer.setSendFileUrl(SvdProtocolUtil.buildUrl(configuration.getUrl()) + "/sendFile.do");
            camelContext.addRoutes(sendFileRoute);
        }
    }


    private void stopRoutes(Account oldAccount) throws Exception {
        stopGetRoutes(oldAccount);
        stopPushRoutes(oldAccount);
    }

    private void stopGetRoutes(Account account) throws Exception {
        GetFilesRouteConsumer getFilesRouteConsumer = getFilesRouteConsumerMap.get(account.getLogin());
        if (getFilesRouteConsumer != null && getFilesRouteConsumer.getAccount().getLogin().equals(account.getLogin())) {
            getFilesRouteConsumerMap.remove(account.getLogin());
            camelContext.stopRoute("route-get-" + account.getLogin());
            camelContext.removeRoute("route-get-" + account.getLogin());
        }
    }

    private void stopPushRoutes(Account account) throws Exception {
        PushFileRouteConsumer pushFileRouteConsumer = pushFileRouteConsumerMap.get(account.getLogin());
        if (pushFileRouteConsumer != null && pushFileRouteConsumer.getAccount().getLogin().equals(account.getLogin())) {
            pushFileRouteConsumerMap.remove(account.getLogin());
            camelContext.stopRoute("route-push-" + account.getLogin());
            camelContext.removeRoute("route-push-" + account.getLogin());
        }
    }

    public void refreshAccountRoutes(Account account) throws UnknownHostException {
        AbstractGetFilesConsumer getFileConsumer = svdTransportProtocolService.getAbstractGetFilesConsumers().get(account.getLogin());
        getFileConsumer.getParams().setPassword(account.getPassword());
        getFileConsumer.getParams().setCompName(InetAddress.getLocalHost().getHostName());
        getFileConsumer.getParams().setServId(account.getServerId());

        GetFilesRouteConsumer getFilesRouteConsumer = getFilesRouteConsumerMap.get(account.getLogin());
        getFilesRouteConsumer.setAccount(account);
        getFilesRouteConsumer.setSaveFileEndpoint("file:" + (account.getReceiveDir() == null ? "/" : account.getReceiveDir() + "?charset=utf-8"));

        PushFileRouteConsumer pushFileRouteConsumer = pushFileRouteConsumerMap.get(account.getLogin());
        pushFileRouteConsumer.setAccount(account);
    }

    public void restartPushFilesRoute(Account account) throws Exception {
        stopPushRoutes(account);
        applicationEventPublisher.publishEvent(new PushFileRouteStart(this, account));
    }


    public void startPushFilesRoute(Account account, Configuration configuration) throws Exception {
        PushFileRouteConsumer pushFileRouteConsumer = new PushFileRouteConsumer(svdTransportProtocolService, svdTransportProtocolSendParams);

        pushFileRouteConsumer.setAccount(account);
        pushFileRouteConsumer.setServerUsername(configuration.getServerUsername());
        pushFileRouteConsumerMap.put(account.getLogin(), pushFileRouteConsumer);

        PushFileRoute pushFileRoute = new PushFileRoute(operationLogService, pushFileRouteConsumer);
        pushFileRoute.setFromEndpoint("file:" +
                (account.getSendDir() == null || account.getSendDir().isEmpty() ?
                        "./data/send?delete=true"
                        :
                        account.getSendDir() + "?delete=true")
        );

        camelContext.addRoutes(pushFileRoute);
    }

    private void configurationRefresh(Configuration configuration) {
        svdTransportProtocolService.getAbstractGetFilesConsumers().forEach((s, getFileConsumer) -> {
            getFileConsumer.setGetFile(SvdProtocolUtil.buildUrl(configuration.getUrl()) + "/getFiles.do?" + configurationToStringConfig(configuration));
            getFileConsumer.setDeleteFiles(SvdProtocolUtil.buildUrl(configuration.getUrl()) + "/deleteFile.do?" + configurationToStringConfig(configuration));
        });
//        configuration.getAccounts().forEach(account -> {
//            AbstractGetFilesConsumer getFilesConsumer = svdTransportProtocolService.getAbstractGetFilesConsumers().get(account.getLogin());
//            if (getFilesConsumer instanceof GetFileLongPollConsumer){
//                String pushUrl = configuration.getPushServerUrl().endsWith("/") ? configuration.getPushServerUrl() : configuration.getPushServerUrl() + "/";
//                ((GetFileLongPollConsumer) getFilesConsumer).setUrl(pushUrl + account.getServerId() + "_" + account.getLogin());
//            }
//        });
        sendFileConsumer.setSendFileUrl(SvdProtocolUtil.buildUrl(configuration.getUrl()) + "/sendFile.do?" + configurationToStringConfig(configuration));
        pushFileRouteConsumerMap.forEach((s, pushFileRouteConsumer) -> {
            pushFileRouteConsumer.setServerUsername(configuration.getServerUsername());
        });
    }

    private String configurationToStringConfig(Configuration configuration) {
        StringBuilder config = new StringBuilder();
        if (configuration.getProxy() != null && configuration.getProxy().isActive()) {
            appendProxyUrl(configuration.getProxy().getUrl(), config);
            config.append("&proxyAuthPort=").append(configuration.getProxy().getPort());
            config.append("&proxyAuthUsername=").append(configuration.getProxy().getLogin());
            config.append("&proxyAuthPassword=").append(configuration.getProxy().getPassword());
            config.append("&proxyAuthMethod=").append(configuration.getProxy().getAuthType());
        }
        config.append("&httpClient.socketTimeout=").append(configuration.getSocketTimeout());
        config.append("&httpClient.connectTimeout=").append(configuration.getConnectionTimeout());
        return config.toString().substring(1);
    }

    private void appendProxyUrl(String proxyUrl, StringBuilder config) {
        String host;
        String scheme;
        try {
            URL url = new URL(proxyUrl);
            host = url.getHost();
            scheme = url.getProtocol();
        } catch (MalformedURLException e) {
            host = proxyUrl;
            scheme = "http";
        }

        config.append("&proxyAuthHost=").append(host);
        config.append("&proxyAuthScheme=").append(scheme);
    }

    public boolean routesExists(Account account) {
        return getFilesRouteConsumerMap.get(account.getLogin()) != null && pushFileRouteConsumerMap.get(account.getLogin()) != null;
    }
}
