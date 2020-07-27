package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadOnlyException;

import java.util.Arrays;

public class ByteArrayDataSource extends DataSource {
	
	private byte[] buffer = new byte[0];
	private boolean readOnly = false; // TODO: implement, test with
	
	public ByteArrayDataSource() {
		// nothing to do
	}
	
	public ByteArrayDataSource(byte[] buffer) {
		this();
		this.buffer = buffer;
	}
	
	public byte[] getBuffer() {
		return this.buffer;
	}
	
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public ByteArrayDataSource setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}
	
	@Override
	public byte[] readDataIn() {
		return Arrays.copyOf(this.buffer, this.buffer.length);
	}
	
	@Override
	public void writeDataOut(byte[] bytes) throws DataSourceReadOnlyException {
		if(this.isReadOnly()) {
			throw new DataSourceReadOnlyException();
		}
		this.buffer = Arrays.copyOf(bytes, bytes.length);
	}
}
