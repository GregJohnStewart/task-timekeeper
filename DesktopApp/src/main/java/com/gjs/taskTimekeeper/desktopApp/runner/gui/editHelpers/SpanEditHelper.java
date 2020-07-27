package com.gjs.taskTimekeeper.desktopApp.runner.gui.editHelpers;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.Collection;

public class SpanEditHelper {
	private static final int COL_SIZE = 15;
	
	private JTextField startField = new JTextField(COL_SIZE);
	private JTextField endField = new JTextField(COL_SIZE);
	private JComboBox<String> taskSelect = new JComboBox<>();
	
	private JButton startNowButton = new JButton("Now");
	private JButton endNowButton = new JButton("Now");
	
	private class NowAction extends AbstractAction {
		private final JTextField field;
		
		private NowAction(JTextField field) {
			super("Now");
			this.field = field;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			this.field.setText(TimeParser.toOutputString(LocalDateTime.now()));
		}
	}
	
	{
		startNowButton.setAction(new NowAction(startField));
		endNowButton.setAction(new NowAction(endField));
	}
	
	public String getStartField() {
		return startField.getText().trim().isEmpty() ? null : startField.getText();
	}
	
	public String getEndField() {
		return endField.getText().trim().isEmpty() ? null : endField.getText();
	}
	
	public String getTaskName() {
		return (String)taskSelect.getSelectedItem();
	}
	
	public JComponent getForm(Collection<Task> tasks) {
		JPanel output = new JPanel();
		output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));
		
		for(Task task : tasks) {
			taskSelect.addItem(task.getName().getName());
		}
		taskSelect.setSelectedIndex(-1);
		
		JPanel temp = new JPanel();
		temp.add(new JLabel("Task: "));
		temp.add(taskSelect);
		output.add(temp);
		
		temp = new JPanel();
		temp.add(new JLabel("Start Datetime:"));
		temp.add(startField);
		temp.add(startNowButton);
		output.add(temp);
		
		temp = new JPanel();
		temp.add(new JLabel("End Datetime:"));
		temp.add(endField);
		temp.add(endNowButton);
		output.add(temp);
		
		return output;
	}
	
	public JComponent getForm(Collection<Task> tasks, Timespan span) {
		JComponent output = this.getForm(tasks);
		this.taskSelect.setSelectedItem(span.getTaskName().getName());
		this.startField.setText(TimeParser.toOutputString(span.getStartTime()));
		this.endField.setText(TimeParser.toOutputString(span.getEndTime()));
		return output;
	}
}
