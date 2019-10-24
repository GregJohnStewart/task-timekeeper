package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;

public abstract class DataSourceTest<T extends DataSource> {
    protected T testSource;

    protected final byte[] testData = "hello world".getBytes();
    protected final byte[] testDataTwo = "olleh dlrow".getBytes();

    public abstract void testEnsureReadWriteCapable() throws ManagerIOException;

    public abstract void testGetInputStream() throws Exception;

    public abstract void testGetOutputStream() throws Exception;
}
