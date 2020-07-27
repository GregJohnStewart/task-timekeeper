package com.gjs.taskTimekeeper.desktopApp.runner.gui.editHelpers;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class AttributeEditor {
	
	private static final int DEFAULT_TEXTAREA_COL = 10;
	
	private List<JTextField> attributes = new ArrayList<>();
	private List<JTextField> values = new ArrayList<>();
	private JPanel attValContainer = new JPanel();
	
	public String getAttributes() {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < attributes.size(); i++) {
			if(attributes.get(i).getText().trim().isEmpty()) {
				continue;
			}
			builder.append(attributes.get(i).getText());
			builder.append(',');
			builder.append(values.get(i).getText());
			builder.append(';');
		}
		
		return builder.toString();
	}
	
	public JPanel getForm() {
		JPanel output = new JPanel();
		output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));
		attValContainer.setLayout(new BoxLayout(attValContainer, BoxLayout.Y_AXIS));
		
		output.add(new JLabel("Attributes:                                         "));
		JPanel temp = new JPanel();
		
		temp.add(new JLabel("Name               "));
		temp.add(new JLabel("Value              "));
		attValContainer.add(temp);
		
		//		output.add(attValContainer);
		
		temp = new JPanel();
		//		temp.setAlignmentX( Component.LEFT_ALIGNMENT );
		JButton addAtt = new JButton("a");
		addAtt.setAction(
			new AbstractAction("Add Attribute") {
				@Override
				public void actionPerformed(ActionEvent e) {
					log.debug("Adding new empty att/val input pair");
					JTextField newAttName = new JTextField(DEFAULT_TEXTAREA_COL);
					JTextField newAttVal = new JTextField(DEFAULT_TEXTAREA_COL);
					
					JPanel newPanel = new JPanel();
					JButton remButton = new JButton("R");
					remButton.setAction(
						new AbstractAction("Rem") {
							@Override
							public void actionPerformed(ActionEvent e) {
								int index = attributes.indexOf(newAttName);
								attributes.remove(index);
								values.remove(index);
								attValContainer.remove(newPanel);
								attValContainer.updateUI();
							}
						});
					remButton.setMargin(new Insets(-2, 0, -2, 0));
					
					newPanel.add(newAttName);
					newPanel.add(newAttVal);
					newPanel.add(remButton);
					attValContainer.add(newPanel);
					attValContainer.updateUI();
					
					attributes.add(newAttName);
					values.add(newAttVal);
				}
			});
		
		temp.add(addAtt);
		
		JScrollPane scrollPane = new JScrollPane();
		Dimension size = new Dimension(350, 150);
		scrollPane.setMaximumSize(size);
		scrollPane.setMinimumSize(size);
		scrollPane.setPreferredSize(size);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(attValContainer);
		
		output.add(scrollPane);
		output.add(temp);
		
		return output;
	}
	
	public JPanel getForm(Map<String, String> attMap) {
		JPanel output = this.getForm();
		
		for(Map.Entry<String, String> entry : attMap.entrySet()) {
			JTextField newAttName = new JTextField(entry.getKey(), DEFAULT_TEXTAREA_COL);
			JTextField newAttVal = new JTextField(entry.getValue(), DEFAULT_TEXTAREA_COL);
			
			JPanel newPanel = new JPanel();
			JButton remButton = new JButton("R");
			remButton.setAction(
				new AbstractAction("Remove") {
					@Override
					public void actionPerformed(ActionEvent e) {
						attributes.remove(newAttName);
						values.remove(newAttVal);
						attValContainer.remove(newPanel);
						attValContainer.updateUI();
					}
				});
			remButton.setMargin(new Insets(-2, 0, -2, 0));
			
			newPanel.add(newAttName);
			newPanel.add(newAttVal);
			newPanel.add(remButton);
			attValContainer.add(newPanel);
			attValContainer.updateUI();
			
			attributes.add(newAttName);
			values.add(newAttVal);
		}
		
		return output;
	}
}
