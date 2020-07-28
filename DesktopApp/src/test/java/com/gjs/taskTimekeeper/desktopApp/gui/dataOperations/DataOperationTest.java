package com.gjs.taskTimekeeper.desktopApp.gui.dataOperations;

import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import com.gjs.taskTimekeeper.baseCode.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.FileDataSource;
import com.gjs.taskTimekeeper.desktopApp.gui.GuiTest;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class DataOperationTest extends GuiTest {
	
	protected Outputter ioOutputter = new Outputter(
		new ByteArrayOutputStream(),
		new ByteArrayOutputStream()
	);
	protected ManagerIO managerIO;
	
	protected void setupManagerIo(File file) {
		this.managerIO = new ManagerIO(
			new FileDataSource(file),
			this.ioOutputter
		);
	}
}
