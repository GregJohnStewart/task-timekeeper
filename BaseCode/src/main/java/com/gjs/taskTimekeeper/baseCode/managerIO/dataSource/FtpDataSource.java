package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;

/** https://www.baeldung.com/java-ftp-client */
public class FtpDataSource extends DataSource {
    @Override
    public void ensureReadWriteCapable() throws ManagerIOException {}

    @Override
    public byte[] readDataIn() throws ManagerIOException {
        return new byte[0];
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
