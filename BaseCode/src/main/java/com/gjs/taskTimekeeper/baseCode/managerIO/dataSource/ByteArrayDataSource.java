package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import java.util.Arrays;

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
    public byte[] readDataIn() {
        return Arrays.copyOf(this.buffer, this.buffer.length);
    }

    @Override
    public void writeDataOut(byte[] bytes) {
        this.buffer = bytes;
    }
}
