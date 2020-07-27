package com.gjs.taskTimekeeper.desktopApp.gui.utils.matcher;

import org.assertj.swing.core.GenericTypeMatcher;

import javax.swing.*;

public class ButtonTextMatcher extends GenericTypeMatcher<JButton> {
	
	private final String buttonText;
	
	public ButtonTextMatcher(String buttonText) {
		super(JButton.class);
		this.buttonText = buttonText;
	}
	
	@Override
	protected boolean isMatching(JButton component) {
		return component.getText().equalsIgnoreCase(this.buttonText);
	}
}
