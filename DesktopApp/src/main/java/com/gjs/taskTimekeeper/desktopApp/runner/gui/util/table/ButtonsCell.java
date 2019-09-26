package com.gjs.taskTimekeeper.desktopApp.runner.gui.util.table;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.List;

public class ButtonsCell
	extends AbstractCellEditor
	implements TableCellEditor, TableCellRenderer {

	private JPanel panel;

	public ButtonsCell() {
		this.updateData(Collections.emptyList());
	}

	private void updateData(List<JButton> buttons) {
		this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));


		for(JButton button : buttons) {
			button.setMargin(new Insets(-2, 0, -2, 0));

			this.panel.add(button);
		}
	}

	private void updateData(List<JButton> buttons, boolean isSelected, JTable table) {
		this.updateData(buttons);

		if (isSelected) {
			this.panel.setBackground(table.getSelectionBackground());
		}else{
			this.panel.setBackground(table.getBackground());
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
	                                             boolean isSelected, int row, int column) {
		this.updateData((List<JButton>)value, isSelected, table);
		return panel;
	}

	public Object getCellEditorValue() {
		return null;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
	                                               boolean isSelected, boolean hasFocus, int row, int column) {
		this.updateData((List<JButton>)value, isSelected, table);
		return panel;
	}
}
