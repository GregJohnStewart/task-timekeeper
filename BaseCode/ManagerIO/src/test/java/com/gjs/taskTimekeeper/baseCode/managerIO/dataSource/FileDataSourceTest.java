package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadOnlyException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceWriteException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class FileDataSourceTest extends DataSourceTest<FileDataSource> {
	
	private final File testFileFolder =
		new File(
			FileDataSourceTest.class
				.getClassLoader()
				.getResource("testTimeManagerData/")
				.getFile());
	private final File testFile = new File(testFileFolder.getPath() + "/temp" + RandomStringUtils.randomAlphanumeric(4) + ".tks");
	
	public FileDataSourceTest() {
		log.debug("Test file location: {}", testFile);
		this.testSource = new FileDataSource(testFile);
	}
	
	@AfterEach
	public void cleanup() {
		this.testFileFolder.setWritable(true);
		if(testFile.exists() && testFile.isFile()) {
			testFile.setWritable(true);
			testFile.delete();
		}
	}
	
	private void createFile() throws IOException {
		try {
			if(!this.testFile.createNewFile()) {
				throw new RuntimeException("Test file could NOT be created. (" + this.testFile.getPath() + ")");
			}
		} catch(IOException e) {
			log.error("Test file could not be created. File: '{}' ", this.testFile.getPath(), e);
			throw e;
		}
	}
	
	@Test
	public void altConstructorTest() throws MalformedURLException {
		this.testSource = new FileDataSource(new URL("file://" + this.testFile.getPath()));
		this.testSource = new FileDataSource(this.testFile.getPath());
	}
	
	// <editor-fold desc="Ensure read/write tests">
	@Test
	public void ensureReadWriteCapableTest() throws IOException {
		this.createFile();
		this.testSource.ensureReadWriteCapable();
	}
	
	@Test
	public void ensureReadWriteCapableActuallyADirTest() throws IOException {
		this.testSource = new FileDataSource(testFileFolder);
		Assertions.assertThrows(ManagerIOException.class, ()->{
			this.testSource.ensureReadWriteCapable();
		});
	}
	
	@Test
	public void ensureReadWriteCapableReadOnlyDirTest() throws IOException {
		testFileFolder.setWritable(false);
		Assertions.assertThrows(ManagerIOException.class, ()->{
			this.testSource.ensureReadWriteCapable();
		});
	}
	
	@Test
	public void ensureReadWriteCapableCreateNewFileTest() throws IOException {
		this.testSource.ensureReadWriteCapable();
	}
	
	@Test
	public void ensureReadWriteCapableNoCreateNewFileTest() throws IOException {
		Assertions.assertThrows(ManagerIOException.class, ()->{
			this.testSource.ensureReadWriteCapable(false);
		});
	}
	
	@Test
	public void ensureReadWriteCapableCantReadTest() throws IOException {
		this.createFile();
		testFile.setReadable(false);
		Assertions.assertThrows(DataSourceReadException.class, ()->{
			this.testSource.ensureReadWriteCapable();
		});
	}
	
	@Test
	public void ensureReadWriteCapableCantWriteTest() throws IOException {
		this.createFile();
		testFile.setWritable(false);
		Assertions.assertThrows(DataSourceReadOnlyException.class, ()->{
			this.testSource.ensureReadWriteCapable();
		});
	}
	// </editor-fold>
	
	@Test
	public void readOnlyTest() throws IOException {
		this.createFile();
		
		assertFalse(this.testSource.isReadOnly());
		
		this.testFile.setWritable(false);
		assertTrue(this.testSource.isReadOnly());
	}
	
	@Test
	public void readWriteTest() throws IOException {
		this.testSource.ensureReadWriteCapable(true);
		
		this.testSource.writeDataOut(this.testData);
		assertArrayEquals(this.testData, this.testSource.readDataIn());
	}
	
	@Test
	public void readNoFileTest() throws IOException {
		Assertions.assertThrows(DataSourceReadException.class, ()->{
			this.testSource.readDataIn();
		});
	}
	
	@Test
	public void writeNoFileTest() throws IOException {
		this.testSource.writeDataOut(this.testData);
		assertArrayEquals(this.testData, this.testSource.readDataIn());
	}
	
	@Test
	public void writeReadOnlyTest() throws IOException {
		this.createFile();
		this.testFile.setWritable(false);
		
		Assertions.assertThrows(DataSourceWriteException.class, ()->{
			this.testSource.writeDataOut(this.testData);
		});
	}
}
