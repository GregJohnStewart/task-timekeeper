package com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class OpenUrlOnClickListener extends MouseAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenUrlOnClickListener.class);

    private URI uri;

    public OpenUrlOnClickListener(URI uri) {
        this.uri = uri;
    }

    private void open() {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            LOGGER.debug("Opening url: {}", this.uri);
            try {
                desktop.browse(this.uri);
            } catch (Exception e) {
                LOGGER.error("Failed to open url. Error: ", e);
            }
        } else {
            LOGGER.warn("Opening url on desktop not supported.");
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        super.mouseClicked(mouseEvent);
        LOGGER.trace("Mouse clicked");
        this.open();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        LOGGER.trace("mouse pressed.");
        this.open();
    }
}
