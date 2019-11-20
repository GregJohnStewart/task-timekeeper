package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadOnlyException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import java.io.IOException;
import org.junit.Test;

public class ByteArrayDataSourceTest extends DataSourceTest<ByteArrayDataSource> {

    public ByteArrayDataSourceTest() {
        this.testSource = new ByteArrayDataSource(this.testData);
    }

    @Test
    public void testEnsureReadWriteCapable() throws ManagerIOException {
        new ByteArrayDataSource().ensureReadWriteCapable();
    }

    @Test(expected = DataSourceReadOnlyException.class)
    public void testEnsureReadWriteCapableReadOnly() throws ManagerIOException {
        new ByteArrayDataSource().setReadOnly(true).ensureReadWriteCapable();
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

    @Test(expected = DataSourceReadOnlyException.class)
    public void testWriteDataOutReadOnly() throws IOException {
        this.testSource.setReadOnly(true);
        this.testSource.writeDataOut(this.testDataTwo);
    }
}
