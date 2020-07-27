package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadOnlyException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

//TODO:: make these source tests part of a larger abstract class?
public class ByteArrayDataSourceTest extends DataSourceTest<ByteArrayDataSource> {
	
	public ByteArrayDataSourceTest() {
		this.testSource = new ByteArrayDataSource(this.testData);
	}
	
	@Test
	public void testEnsureReadWriteCapable() throws ManagerIOException {
		new ByteArrayDataSource().ensureReadWriteCapable();
	}
	
	@Test
	public void testEnsureReadWriteCapableReadOnly() throws ManagerIOException {
		Assertions.assertThrows(DataSourceReadOnlyException.class, ()->{
			new ByteArrayDataSource().setReadOnly(true).ensureReadWriteCapable();
		});
	}
	
	@Test
	public void testReadDataIn() throws IOException {
		assertArrayEquals(this.testData, this.testSource.readDataIn());
	}
	
	@Test
	public void testWriteDataOut() throws IOException {
		this.testSource.writeDataOut(this.testDataTwo);
		assertArrayEquals(this.testDataTwo, this.testSource.getBuffer());
	}
	
	@Test
	public void testWriteDataOutReadOnly() throws IOException {
		this.testSource.setReadOnly(true);
		
		Assertions.assertThrows(DataSourceReadOnlyException.class, ()->{
			this.testSource.writeDataOut(this.testDataTwo);
		});
	}
}
