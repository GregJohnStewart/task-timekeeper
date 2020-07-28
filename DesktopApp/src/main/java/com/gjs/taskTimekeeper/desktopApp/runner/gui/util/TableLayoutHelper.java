package com.gjs.taskTimekeeper.desktopApp.runner.gui.util;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableLayoutHelper extends JPanel {
	
	private static final Border BORDER =
		new LineBorder(Color.GRAY, 1); // new BevelBorder(BevelBorder.LOWERED);
	private static final Color DEFAULT_ROW_COLOR =
		Color.WHITE; // new BevelBorder(BevelBorder.LOWERED);
	private static final int ROW_HEIGHT = 19;
	private static final Insets BUTTON_INSETS = new Insets(-2, 0, -2, 0);
	
	private TableLayoutHelper(
		String name,
		List<List<Object>> data,
		List<String> headers,
		Map<Integer, Double> colWidths,
		Map<Integer, Color> rowColors
	) {
		super();
		this.setName(name);
		
		int numCols = headers.size();
		int numRows = data.size();
		
		double[] widths = new double[numCols];
		double[] heights = new double[numRows + 1];
		
		for(int i = 0; i < widths.length; i++) {
			widths[i] = colWidths.getOrDefault(i, TableLayout.FILL);
		}
		Arrays.fill(heights, ROW_HEIGHT);
		
		TableLayout layout = new TableLayout(new double[][]{widths, heights});
		
		this.setLayout(layout);
		
		this.addHeaders(headers);
		this.addContent(data, rowColors);
	}
	
	public TableLayoutHelper(
		String name,
		final Container pane,
		List<List<Object>> data,
		List<String> headers,
		Map<Integer, Double> colWidths,
		Map<Integer, Color> rowColors
	) {
		this(name, data, headers, colWidths, rowColors);
		this.addToPane(pane);
	}
	
	public TableLayoutHelper(
		String name,
		final JScrollPane pane,
		List<List<Object>> data,
		List<String> headers,
		Map<Integer, Double> colWidths,
		Map<Integer, Color> rowColors
	) {
		this(name, data, headers, colWidths, rowColors);
		this.addToPane(pane);
	}
	
	public TableLayoutHelper(
		String name,
		final JScrollPane pane,
		List<List<Object>> data,
		List<String> headers,
		Map<Integer, Double> colWidths
	) {
		this(name, data, headers, colWidths, new HashMap<>());
		this.addToPane(pane);
	}
	
	public TableLayoutHelper(
		String name,
		final JScrollPane pane,
		List<List<Object>> data,
		List<String> headers
	) {
		this(name, data, headers, new HashMap<>(), new HashMap<>());
		this.addToPane(pane);
	}
	
	public void addToPane(final Container pane) {
		pane.add(this, BorderLayout.NORTH);
	}
	
	public void addToPane(final JScrollPane pane) {
		pane.setViewportView(this);
	}
	
	public void addHeaders(List<String> headers) {
		int count = 0;
		for(String header : headers) {
			JLabel newLabel = new JLabel(header);
			Font f = newLabel.getFont();
			
			newLabel.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
			newLabel.setBorder(BORDER);
			this.add(newLabel, new TableLayoutConstraints(count, 0));
			count++;
		}
	}
	
	public void addContent(List<List<Object>> data, Map<Integer, Color> rowColors) {
		int i = 1;
		for(List<Object> row : data) {
			addContentToTablePanel(this, row, i, rowColors.getOrDefault(i, DEFAULT_ROW_COLOR));
			i++;
		}
	}
	
	private static void addToPanel(JPanel panelToAddTo, Component component, int col, int row) {
		panelToAddTo.add(component, new TableLayoutConstraints(col, row));
	}
	
	private static JLabel makeLabel(String text, Color bgColor) {
		JLabel newLabel = new JLabel(text);
		newLabel.setBackground(bgColor);
		newLabel.setOpaque(true);
		newLabel.setBorder(BORDER);
		return newLabel;
	}
	
	private static void addContentToPanel(JPanel panelToAddTo, List<Object> content)
		throws IllegalArgumentException {
		for(Object item : content) {
			if(item instanceof String) {
				panelToAddTo.add(new Label((String)item));
			} else if(item instanceof Number || item instanceof Character) {
				panelToAddTo.add(new Label(String.valueOf(item)));
			} else if(item instanceof JButton) {
				((JButton)item).setMargin(BUTTON_INSETS);
				panelToAddTo.add((Component)item);
			} else if(item instanceof Component) {
				panelToAddTo.add((Component)item);
			} else if(item instanceof List) {
				JPanel innerPanel = new JPanel();
				addContentToPanel(innerPanel, (List)item);
				panelToAddTo.add(innerPanel);
			} else {
				throw new IllegalArgumentException(
					"Can't add object of type: " + item.getClass().getName());
			}
		}
	}
	
	private static void addContentToTablePanel(
		JPanel panelToAddTo, List<Object> content, int row, Color bgColor
	)
		throws IllegalArgumentException {
		int col = 0;
		for(Object item : content) {
			if(item instanceof String) {
				addToPanel(panelToAddTo, makeLabel((String)item, bgColor), col, row);
			} else if(item instanceof Number || item instanceof Character) {
				addToPanel(panelToAddTo, makeLabel(String.valueOf(item), bgColor), col, row);
			} else if(item instanceof Component) {
				addToPanel(panelToAddTo, (Component)item, col, row);
			} else if(item instanceof List) {
				JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
				innerPanel.setBackground(bgColor);
				innerPanel.setBorder(BORDER);
				addContentToPanel(innerPanel, (List)item);
				addToPanel(panelToAddTo, innerPanel, col, row);
			} else {
				throw new IllegalArgumentException(
					"Can't add object of type: " + item.getClass().getName());
			}
			col++;
		}
	}
}
