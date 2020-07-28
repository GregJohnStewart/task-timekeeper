package com.gjs.taskTimekeeper.desktopApp.runner.gui.util;

import javax.swing.*;

public abstract class IndexAction extends AbstractAction {
	private int index;
	
	public IndexAction(String name, int index) {
		super(name);
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
