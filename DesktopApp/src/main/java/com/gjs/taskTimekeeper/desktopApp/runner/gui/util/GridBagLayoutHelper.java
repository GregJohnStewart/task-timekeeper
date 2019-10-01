package com.gjs.taskTimekeeper.desktopApp.runner.gui.util;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Label;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridBagLayoutHelper extends JPanel {

	private static final Border BORDER = new BevelBorder(BevelBorder.LOWERED);

	private GridBagLayoutHelper(List<List<Object>> data, List<String> headers, Map<Integer, Double> colWidths) {
		super();

		int numCols = headers.size();
		int numRows = data.size();

		double widths[] = new double[numCols];
		double heights[] = new double[numRows + 1];

		for(int i = 0; i < widths.length; i++){
			widths[i] = colWidths.getOrDefault(i, TableLayout.FILL);
		}
		for(int i = 0; i < heights.length; i++){
			heights[i] = 25;
		}

		TableLayout layout = new TableLayout(new double[][]{widths, heights});
		//GridBagLayout layout = new GridBagLayout();// data.size() + 1, headers.length

		//TODO:: get the gridbag layout to properly have the columns sized https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
		this.setLayout(layout);

		this.addHeaders(headers);
		this.addContent(data);
	}
	public GridBagLayoutHelper(final Container pane, List<List<Object>> data, List<String> headers, Map<Integer, Double> colWidths) {
		this(data, headers, colWidths);
		this.addToPane(pane);
	}
	public GridBagLayoutHelper(final JScrollPane pane, List<List<Object>> data, List<String> headers, Map<Integer, Double> colWidths) {
		this(data, headers, colWidths);
		this.addToPane(pane);
	}
	public GridBagLayoutHelper(final JScrollPane pane, List<List<Object>> data, List<String> headers) {
		this(data, headers, new HashMap<>());
		this.addToPane(pane);
	}

	public void addToPane(final Container pane){
		pane.add(this, BorderLayout.NORTH);
	}

	public void addToPane(final JScrollPane pane){
		pane.setViewportView(this);
	}

	public void addHeaders(List<String> headers){
		int count = 0;
		for(String header : headers) {
			JLabel newLabel = new JLabel(header);
			Font f = newLabel.getFont();

			newLabel.setFont(
				f.deriveFont(
					f.getStyle() | Font.BOLD
				)
			);
			newLabel.setBorder(BORDER);
			this.add(newLabel, new TableLayoutConstraints(count, 0));
			count++;
		}
	}

	public void addContent(List<List<Object>> data){
		int i = 1;
		for(List<Object> row : data){
			addContentToTablePanel(this, row, i);
			i++;
		}
	}

	private static void addToPanel(JPanel panelToAddTo, Component component, int col, int row){
		panelToAddTo.add(component, new TableLayoutConstraints(col, row));
	}

	private static JLabel makeLabel(String text){
		JLabel newLabel = new JLabel(text);
		newLabel.setBorder(BORDER);
		return newLabel;
	}

	private static void addContentToPanel(JPanel panelToAddTo, List<Object> content) throws IllegalArgumentException {
		int i = 0;
		for(Object item : content){
			if(item instanceof String) {
				panelToAddTo.add(new Label((String) item));
			} else if(item instanceof Number || item instanceof Character){
				panelToAddTo.add(new Label(String.valueOf(item)));
			} else if(item instanceof JButton){
				((JButton) item).setMargin(new Insets(-1, 0, -1, 0));
				panelToAddTo.add((Component) item);
			} else if(item instanceof Component){
				panelToAddTo.add((Component) item);
			} else if(item instanceof List) {
				JPanel innerPanel = new JPanel();
				addContentToPanel(innerPanel, (List) item);
				panelToAddTo.add(innerPanel);
			} else {
				throw new IllegalArgumentException("Can't add object of type: " + item.getClass().getName());
			}
			i++;
		}
	}

	private static void addContentToTablePanel(JPanel panelToAddTo, List<Object> content, int row) throws IllegalArgumentException {
		int col = 0;
		for(Object item : content){
			if(item instanceof String) {
				addToPanel(panelToAddTo, makeLabel((String) item), col, row);
			} else if(item instanceof Number || item instanceof Character){
				addToPanel(panelToAddTo, makeLabel(String.valueOf(item)), col, row);
			} else if(item instanceof Component){
				addToPanel(panelToAddTo, (Component) item, col, row);
			} else if(item instanceof List) {
				JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				innerPanel.setBorder(BORDER);
				addContentToPanel(innerPanel, (List) item);
				addToPanel(panelToAddTo, innerPanel, col, row);
			} else {
				throw new IllegalArgumentException("Can't add object of type: " + item.getClass().getName());
			}
			col++;
		}
	}
}
