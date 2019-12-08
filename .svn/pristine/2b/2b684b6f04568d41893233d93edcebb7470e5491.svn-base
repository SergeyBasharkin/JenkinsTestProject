package ru.alta.svd.client.web.main.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.alta.svd.client.web.main.condition.TrayCondition;
import ru.alta.svd.client.web.main.tray.TrayMenu;

import java.awt.*;

@Configuration
@Profile({"Standalone", "JWS", "test"})
public class TrayConfig {
    @Value("${svd.tray.img-path}")
    private String IMAGE_PATH;

    @Value("${svd.tray.tooltip}")
    private String TOOLTIP;

    @Bean
    @Conditional(TrayCondition.class)
    public TrayMenu trayMenu() {
        return new TrayMenu(TrayMenu.createImage(IMAGE_PATH, TOOLTIP), TOOLTIP, new PopupMenu(), SystemTray.getSystemTray());
    }

}
