package ru.alta.svd.client.rest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.alta.svd.client.core.domain.entity.OperationLog;
import ru.alta.svd.client.core.dto.OperationFilter;
import ru.alta.svd.client.core.dto.Page;
import ru.alta.svd.client.core.dto.PageRequest;
import ru.alta.svd.client.core.service.api.OperationLogService;
import ru.alta.svd.client.rest.exception.BadRequestException;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author pechenko
 */
@RestController
@RequestMapping(value = "/operation", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class OperationController {

    private final OperationLogService operationLogService;

    @ApiOperation(value = "Список операций")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page.")
    })
    public Page<OperationLog> list(@ModelAttribute OperationFilter filter,
                                   @ApiIgnore PageRequest pageRequest) {
        if (filter.getLogType() == null || filter.getLogin() == null)
            throw new BadRequestException();

        return operationLogService.findBy(filter, pageRequest);
    }

    @ApiOperation(value = "Отчиска лога")
    @RequestMapping(value = "/clear", method = RequestMethod.DELETE)
    public void clear() {
        operationLogService.clear();
    }
}
