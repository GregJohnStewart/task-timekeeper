package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

public abstract class DataSourceTest <T extends DataSource> {
	protected T testSource;
	
	protected final byte[] testData = "hello world".getBytes();
	protected final byte[] testDataTwo = "olleh dlrow".getBytes();
}
