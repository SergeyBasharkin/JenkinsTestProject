package ru.alta.svd.client.web.main.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import ru.alta.svd.client.rest.config.SvdClientRestConfig;

@Configuration
@Import({TrayConfig.class, SvdClientRestConfig.class})
@ComponentScan(basePackages = {
        "ru.alta.svd.client.web.main.listener",
        "ru.alta.svd.client.web.main.command"
})
public class SvdClientWebConfig {
}
