package com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.net.URI;

public class OpenUrlOnClickListener extends MouseAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenUrlOnClickListener.class);

	private URI uri;

	public OpenUrlOnClickListener(URI uri){
		this.uri = uri;
	}

	private void open(){
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
		LOGGER.debug("Mouse clicked");
		this.open();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		LOGGER.debug("mouse pressed.");
		this.open();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		LOGGER.debug("mouse released.");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		LOGGER.debug("mouse entered.");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		LOGGER.debug("mouse exited.");
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		super.mouseWheelMoved(e);
		LOGGER.debug("mouse wheel moved.");
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		LOGGER.debug("mouse dragged.");
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		LOGGER.debug("mouse moved.");
	}
}
