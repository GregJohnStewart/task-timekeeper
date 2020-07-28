package com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

@Slf4j
public class OpenUrlOnClickListener extends MouseAdapter {
    
    private URI uri;
    
    public OpenUrlOnClickListener(URI uri) {
        this.uri = uri;
    }
    
    private void open() {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            log.debug("Opening url: {}", this.uri);
            try {
                desktop.browse(this.uri);
            } catch (Exception e) {
                log.error("Failed to open url. Error: ", e);
            }
        } else {
            log.warn("Opening url on desktop not supported.");
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        super.mouseClicked(mouseEvent);
        log.trace("Mouse clicked");
        this.open();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        log.trace("mouse pressed.");
        this.open();
    }
}
