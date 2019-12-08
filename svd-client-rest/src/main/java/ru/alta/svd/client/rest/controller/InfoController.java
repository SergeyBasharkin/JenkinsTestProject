package ru.alta.svd.client.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alta.svd.client.rest.dto.BuildInfo;

@RestController
@RequestMapping("/info")
public class InfoController {

    private final BuildInfo buildInfo;

    public InfoController(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
    }

    @GetMapping("/build")
    public BuildInfo buildInfo(){
        return buildInfo;
    }
}
