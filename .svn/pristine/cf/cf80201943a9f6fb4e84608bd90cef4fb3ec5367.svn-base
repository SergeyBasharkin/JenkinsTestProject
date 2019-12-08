package ru.alta.svd.client.core.route;

import lombok.Getter;
import lombok.Setter;
import ru.alta.svd.client.core.route.consumers.GetFilesRouteConsumer;
import ru.alta.svd.client.core.service.api.OperationLogService;

@Getter
@Setter
public class GetFilesRoute extends AbstractAccountRoute {

    private final GetFilesRouteConsumer consumer;

    public GetFilesRoute(OperationLogService operationLogService, GetFilesRouteConsumer consumer) {
        super(operationLogService);
        this.consumer = consumer;
    }

    @Override
    public void configureInternal() throws Exception {
        from( "direct:getFileTo" + consumer.getAccount().getLogin())
                .routeId("route-get-"+consumer.getAccount().getLogin())
                .bean(consumer, "xmlToFile")
                .bean(consumer, "recognizeSaveFileEndpoint")
                .bean(consumer, "checkFiles");
    }
}
