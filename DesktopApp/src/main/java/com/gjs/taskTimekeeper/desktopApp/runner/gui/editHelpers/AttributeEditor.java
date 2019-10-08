package com.gjs.taskTimekeeper.desktopApp.runner.gui.editHelpers;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeEditor {

	List<JTextField> attributes = new ArrayList<>();
	List<JTextField> values = new ArrayList<>();
	List<JPanel> attValPanels = new ArrayList<>();


	public String getAttributes(){
		StringBuilder builder = new StringBuilder();

		for(int i = 0; i < attributes.size(); i++){
			builder.append(attributes.get(i));
			builder.append(',');
			builder.append(values.get(i));
			builder.append(';');
		}

		return builder.toString();
	}


	public JComponent getForm(Map<String, String> attMap){
		JPanel output = new JPanel();
		output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));

		output.add(new JLabel("Attributes:"));

		//TODO:: add and do this.

		return output;
	}
}
