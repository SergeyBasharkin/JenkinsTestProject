package ru.alta.svd.client.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.alta.svd.client.core.config.SvdClientCoreConfig;
import ru.alta.svd.client.rest.dto.BuildInfo;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author pechenko
 */
@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {
    "ru.alta.svd.client.rest.controller"
})
@Import({SvdClientCoreConfig.class, FormatterConfig.class})
public class SvdClientRestConfig {

    @Value("${svd.client.build-time:}")
    private String buildTime;

    @Value("${svd.send.client-version:1.0.1}")
    private String version;

    @Bean
    public BuildInfo buildInfo(){
        return BuildInfo.builder()
                .buildDate(buildTime)
                .version(version)
                .build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("(?!/error).+"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SVD-Client REST API")
                .version("1.1.1")
                .contact(new Contact(null, "https://www.alta.ru", "support@alta.ru"))
                .build();
    }
}
