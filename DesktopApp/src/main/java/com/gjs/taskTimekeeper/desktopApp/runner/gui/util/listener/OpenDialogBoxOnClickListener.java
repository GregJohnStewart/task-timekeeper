package com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OpenDialogBoxOnClickListener extends MouseAdapter {
	
	private final Component parent;
	private Object message;
	private String title;
	private int messageType;
	private Icon icon;
	
	public OpenDialogBoxOnClickListener(
		Component parent, Object message, String title, int messageType, Icon icon
	) {
		this.parent = parent;
		this.message = message;
		this.title = title;
		this.messageType = messageType;
		this.icon = icon;
	}
	
	public OpenDialogBoxOnClickListener(
		Component parent, Object message, String title, int messageType, Image icon
	) {
		this(parent, message, title, messageType, new ImageIcon(icon));
	}
	
	public OpenDialogBoxOnClickListener(Component parent, Object message, String title, int messageType) {
		this.parent = parent;
		this.message = message;
		this.title = title;
		this.messageType = messageType;
	}
	
	private void showDialogBox() {
		if(this.icon == null) {
			JOptionPane.showInternalMessageDialog(
				this.parent, this.message, this.title, this.messageType);
		} else {
			JOptionPane.showInternalMessageDialog(
				this.parent, this.message, this.title, this.messageType, this.icon);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		this.showDialogBox();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		this.showDialogBox();
	}
}
