package com.gjs.taskTimekeeper.desktopApp.gui.utils;

import com.gjs.taskTimekeeper.desktopApp.runner.commandLine.CliManagerRunnerTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestFileUtils {
	
	public static final File testUiOptionsFile =
		new File(
			CliManagerRunnerTest.class
				.getClassLoader()
				.getResource("testTimeManagerData/testUiConfig.cfg")
				.getFile()
		);
	
	public static final File testUserConfigFile =
		new File(
			CliManagerRunnerTest.class
				.getClassLoader()
				.getResource("test/testUserConfig.properties")
				.getFile()
		);
	
	public static final File fullTestFile =
		new File(
			CliManagerRunnerTest.class
				.getClassLoader()
				.getResource("testTimeManagerData/fully_populated.json")
				.getFile());
	public static final File emptyTestFile =
		new File(
			CliManagerRunnerTest.class
				.getClassLoader()
				.getResource("testTimeManagerData/empty.json")
				.getFile());
	public static final File workingTestFile =
		new File(
			CliManagerRunnerTest.class
				.getClassLoader()
				.getResource("testTimeManagerData/working.json")
				.getFile()
		);
	
	public static void resetWorkingFiles() throws IOException {
		resetWorkingFile(workingTestFile, fullTestFile);
	}
	
	
	public static void resetWorkingFile(File workingFile, File dataTemplate) throws IOException {
		workingFile.delete();
		workingFile.createNewFile();
		
		try(
			InputStream is = new FileInputStream(dataTemplate);
			OutputStream os = new FileOutputStream(workingFile);
		) {
			byte[] buffer = new byte[1024];
			int length;
			while((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		}
	}
}
