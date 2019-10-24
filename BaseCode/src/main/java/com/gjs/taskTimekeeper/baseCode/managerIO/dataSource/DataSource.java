package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class DataSource {

    /**
     * Ensures that the data source can properly read/write the manager data.
     *
     * @throws ManagerIOException If the data source cannot be used
     */
    public abstract void ensureReadWriteCapable() throws ManagerIOException;

    /**
     * Gets the input stream for retrieving a TimeManager
     *
     * @return The input stream for reading in a time manager.
     */
    public abstract InputStream getInputStream();

    /**
     * Gets the output stream for saving a TimeManager that must be closed.
     *
     * @return The output stream for saving a TimeManager
     */
    public abstract OutputStream getOutputStream();
}
