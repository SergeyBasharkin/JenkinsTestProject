package ru.alta.svd.client.core.main;

import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import ru.alta.svd.client.core.config.SvdClientCoreConfig;

@SpringBootApplication
@Import({
        SvdClientCoreConfig.class
})
public class SvdClientCoreApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(SvdClientCoreApplication.class, args);
        CamelSpringBootApplicationController configurableApplicationContextBean = configurableApplicationContext.getBean(CamelSpringBootApplicationController.class);
        configurableApplicationContextBean.run();
    }

}
