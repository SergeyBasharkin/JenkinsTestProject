package ru.alta.svd.client.web.main.listener;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.alta.svd.client.web.main.tray.TrayMenu;

@Component
@Profile({"Standalone", "JWS", "test"})
public class ContainerListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        try {
            TrayMenu trayMenu = event.getApplicationContext().getBean("trayMenu", TrayMenu.class);
            trayMenu.setPort(String.valueOf(event.getEmbeddedServletContainer().getPort()));
        } catch (BeansException ignored){}
    }
}
