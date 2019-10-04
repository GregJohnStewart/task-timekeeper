package com.gjs.taskTimekeeper.desktopApp.runner.gui;

import com.gjs.taskTimekeeper.backend.Task;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TaskEditHelper {
	private static final int COL_SIZE = 10;

	private JTextField nameField = new JTextField(COL_SIZE);

	//TODO:: add attribute fields

	public String getName(){
		return nameField.getText();
	}

	public JComponent getForm(Task task){
		this.nameField.setText(task.getName());

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Name:"));
		myPanel.add(this.nameField);
		return myPanel;
	}
}
