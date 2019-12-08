package ru.alta.svd.client.web.main.tray;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class TrayMenu extends TrayIcon {

    private PopupMenu popup;
    private SystemTray tray;
    private String port;

    public TrayMenu(Image image, String tooltip, PopupMenu popup, SystemTray tray) {
        super(image, tooltip);
        this.popup = popup;
        this.tray = tray;
    }

    @PostConstruct
    private void setup() throws AWTException {
        MenuItem messageItem = new MenuItem("Настройки");
        messageItem.addActionListener(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("http://localhost:"+port));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        popup.add(messageItem);

        MenuItem closeItem = new MenuItem("Выйти");
        closeItem.addActionListener(e -> System.exit(0));
        popup.add(closeItem);
        setPopupMenu(popup);
        tray.add(this);
    }

    public static Image createImage(String path, String description) {
        URL imageURL = TrayMenu.class.getResource(path);
        if (imageURL == null) {
            System.err.println("Image not found. Path: " + path);
            return null;
        } else {
            return new ImageIcon(imageURL, description).getImage();
        }
    }

    public void setPort(String port) {
        this.port = port;
    }
}
