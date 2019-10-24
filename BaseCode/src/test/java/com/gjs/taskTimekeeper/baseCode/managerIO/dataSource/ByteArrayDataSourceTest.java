package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import static org.junit.Assert.*;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        try (InputStream stream = this.testSource.getInputStream()) {
            byte[] gotten = new byte[stream.available()];
            assertEquals(gotten.length, stream.read(gotten));
            assertArrayEquals(this.testData, gotten);
        }
    }

    @Test
    @Override
    public void testGetOutputStream() throws IOException {
        try (OutputStream stream = this.testSource.getOutputStream()) {
            stream.write(this.testDataTwo);
        }
        assertArrayEquals(this.testDataTwo, this.testSource.getBuffer());
    }
}
