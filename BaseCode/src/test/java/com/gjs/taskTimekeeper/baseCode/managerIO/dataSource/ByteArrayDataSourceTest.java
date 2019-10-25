package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import java.io.IOException;
import org.junit.Test;

public class ByteArrayDataSourceTest extends DataSourceTest<ByteArrayDataSource> {

    public ByteArrayDataSourceTest() {
        this.testSource = new ByteArrayDataSource(this.testData);
    }

    @Test
    @Override
    public void testEnsureReadWriteCapable() throws ManagerIOException {
        new ByteArrayDataSource().ensureReadWriteCapable();
    }

    @Test
    @Override
    public void testGetInputStream() throws IOException {
        assertArrayEquals(this.testData, this.testSource.readDataIn());
    }

    @Test
    @Override
    public void testGetOutputStream() throws IOException {
        this.testSource.writeDataOut(this.testDataTwo);
        assertArrayEquals(this.testDataTwo, this.testSource.getBuffer());
    }
}
