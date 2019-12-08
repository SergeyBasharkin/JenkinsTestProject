package ru.alta.svd.client.web.main.main;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import ru.alta.svd.client.web.main.config.SvdClientWebConfig;
import ru.alta.svd.client.web.main.tray.FileLocker;

import java.awt.*;

@SpringBootApplication
@Import({
        SvdClientWebConfig.class
})
public class SvdClientWebApplication {

    public static void main(String[] args) {
        FileLocker fileLocker = new FileLocker();
        if (Thread.currentThread().getName().equals("main") && fileLocker.isAppActive()) {
            System.out.println("Приложение уже запущено");
            System.exit(0);
        }
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SvdClientWebApplication.class);
        builder.headless(GraphicsEnvironment.isHeadless());
        builder.run(args);
    }
}
