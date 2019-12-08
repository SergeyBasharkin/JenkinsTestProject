package ru.alta.svd.client.core.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.core.route.consumers.OperationLogConsumer;

@Component
public class OperationLogRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("scheduler://clear?scheduler=spring&scheduler.cron=0+0+6+?+*+*").routeId("route-operation-cleaner").bean(OperationLogConsumer.class, "clearOperationLog");
    }
}
