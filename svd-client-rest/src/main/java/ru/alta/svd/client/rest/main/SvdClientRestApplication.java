package ru.alta.svd.client.rest.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.alta.svd.client.rest.config.SvdClientRestConfig;

@SpringBootApplication
@Import({
        SvdClientRestConfig.class,
})
public class SvdClientRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SvdClientRestApplication.class, args);
    }
}
