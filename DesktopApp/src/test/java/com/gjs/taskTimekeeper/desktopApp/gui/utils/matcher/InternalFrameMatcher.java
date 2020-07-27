package com.gjs.taskTimekeeper.desktopApp.gui.utils.matcher;

import org.assertj.swing.core.GenericTypeMatcher;

import javax.swing.*;

public class InternalFrameMatcher extends GenericTypeMatcher<JInternalFrame> {
	public InternalFrameMatcher() {
		super(JInternalFrame.class);
	}
	
	@Override
	protected boolean isMatching(JInternalFrame component) {
		return true;
	}
}
