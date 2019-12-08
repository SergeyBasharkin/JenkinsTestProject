package ru.alta.svd.client.core.route;

import lombok.Getter;
import lombok.Setter;
import ru.alta.svd.client.core.route.consumers.PushFileRouteConsumer;
import ru.alta.svd.client.core.service.api.OperationLogService;

@Getter
@Setter
public class PushFileRoute extends AbstractAccountRoute {

    private String fromEndpoint;
    private final PushFileRouteConsumer consumer;

    public PushFileRoute(OperationLogService operationLogService, PushFileRouteConsumer consumer) {
        super(operationLogService);
        this.consumer = consumer;
    }

    @Override
    public void configureInternal() {
        onException(Exception.class).logExhausted(false).logExhaustedMessageHistory(false).delay(2000).to("direct:dead");

        from(fromEndpoint).routeId("route-push-"+consumer.getAccount().getLogin())
                .bean(consumer, "sendFile");
    }


}

