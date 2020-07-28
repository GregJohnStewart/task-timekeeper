package com.gjs.taskTimekeeper.desktopApp.runner.gui.editHelpers;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;

import javax.swing.*;

public class TaskEditHelper extends AttributeEditor {
	private static final int COL_SIZE = 10;
	
	private JTextField nameField = new JTextField(COL_SIZE);
	
	public String getName() {
		return nameField.getText();
	}
	
	@Override
	public JPanel getForm() {
		JPanel output = new JPanel();
		output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));
		
		JPanel temp = new JPanel();
		temp.add(new JLabel("Name: "));
		temp.add(this.nameField);
		
		output.add(temp);
		output.add(super.getForm());
		return output;
	}
	
	public JComponent getForm(Task task) {
		this.nameField.setText(task.getName().getName());
		
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
