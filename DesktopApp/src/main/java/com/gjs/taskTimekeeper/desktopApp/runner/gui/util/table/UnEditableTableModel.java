package com.gjs.taskTimekeeper.desktopApp.runner.gui.util.table;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class UnEditableTableModel extends DefaultTableModel {

	public UnEditableTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}

	public UnEditableTableModel(Vector<?> columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	public UnEditableTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	public UnEditableTableModel(Vector<? extends Vector> data, Vector<?> columnNames) {
		super(data, columnNames);
	}

	public UnEditableTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
