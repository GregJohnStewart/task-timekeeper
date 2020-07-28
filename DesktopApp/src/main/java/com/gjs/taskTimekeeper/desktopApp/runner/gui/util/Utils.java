package com.gjs.taskTimekeeper.desktopApp.runner.gui.util;

import javax.swing.*;

public class Utils {
	
	public static void setColWidth(JTable table, int colIndex, int width) {
		table.getColumnModel().getColumn(colIndex).setWidth(width);
		table.getColumnModel().getColumn(colIndex).setPreferredWidth(width);
		table.getColumnModel().getColumn(colIndex).setMaxWidth(width);
		table.getColumnModel().getColumn(colIndex).setMinWidth(width);
	}
}
