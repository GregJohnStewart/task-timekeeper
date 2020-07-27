package com.gjs.taskTimekeeper.desktopApp.runner.gui.options;

/**
 * TODO:: investigate using lombok
 */
public class GuiOptions {
	private boolean autoSave = false;
	private boolean saveOnExit = false;
	private boolean selectNewPeriods = false;
	
	public GuiOptions() {
	}
	
	public GuiOptions(boolean autoSave, boolean saveOnExit, boolean selectNewPeriods) {
		this.autoSave = autoSave;
		this.saveOnExit = saveOnExit;
		this.selectNewPeriods = selectNewPeriods;
	}
	
	public boolean isAutoSave() {
		return autoSave;
	}
	
	public GuiOptions setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
		return this;
	}
	
	public boolean isSaveOnExit() {
		return saveOnExit;
	}
	
	public GuiOptions setSaveOnExit(boolean saveOnExit) {
		this.saveOnExit = saveOnExit;
		return this;
	}
	
	public boolean isSelectNewPeriods() {
		return selectNewPeriods;
	}
	
	public GuiOptions setSelectNewPeriods(boolean selectNewPeriods) {
		this.selectNewPeriods = selectNewPeriods;
		return this;
	}
}
