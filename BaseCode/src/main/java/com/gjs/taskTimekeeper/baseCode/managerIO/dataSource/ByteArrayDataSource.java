package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArrayDataSource extends DataSource {

    private byte[] buffer = new byte[0];

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
    public void ensureReadWriteCapable() throws ManagerIOException {
        // nothing to do
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(buffer);
    }

    @Override
    public OutputStream getOutputStream() {
        return new WritingByteArrayOutputStream();
    }

    private class WritingByteArrayOutputStream extends ByteArrayOutputStream {
        @Override
        public void close() throws IOException {
            super.close();
            buffer = this.toByteArray();
        }
    }
}
