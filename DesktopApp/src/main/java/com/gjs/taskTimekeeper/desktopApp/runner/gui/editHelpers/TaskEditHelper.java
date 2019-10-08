package com.gjs.taskTimekeeper.desktopApp.runner.gui.editHelpers;

import com.gjs.taskTimekeeper.backend.Task;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TaskEditHelper extends AttributeEditor {
	private static final int COL_SIZE = 10;

	private JTextField nameField = new JTextField(COL_SIZE);

	public String getName(){
		return nameField.getText();
	}

	public JComponent getForm(Task task){
		this.nameField.setText(task.getName());

		JPanel output = new JPanel();
		output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));

		JPanel temp = new JPanel();
		temp.add(new JLabel("Name: "));
		temp.add(this.nameField);

		output.add(temp);
		output.add(super.getForm(task.getAttributes()));
		return output;
	}
}
