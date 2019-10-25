package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadOnlyException;
import java.util.Arrays;

public class ByteArrayDataSource extends DataSource {

    private byte[] buffer = new byte[0];
    private boolean readOnly; // TODO: implement, test with

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
    public void ensureReadWriteCapable() throws ManagerIOException {
        if (this.isReadOnly()) {
            throw new ManagerIOReadOnlyException();
        }
    }

    @Override
    public byte[] readDataIn() {
        return Arrays.copyOf(this.buffer, this.buffer.length);
    }

    @Override
    public void writeDataOut(byte[] bytes) {
        super.writeDataOut(bytes);
        this.buffer = Arrays.copyOf(bytes, bytes.length);
    }
}
