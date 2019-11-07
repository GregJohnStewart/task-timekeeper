package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceParsingException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadOnlyException;
import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);

    /**
     * Parsed a data source from the string given. https://www.baeldung.com/java-url-vs-uri
     *
     * @param source The source url given
     * @return The data source made given the url.
     * @throws DataSourceParsingException If something went wrong parsing the exception
     */
    public static DataSource fromString(String source) throws DataSourceParsingException {
        LOGGER.info("Parsing data source.");
        LOGGER.debug("String to parse data source from: {}", source);

        URL sourceUrl;
        try {
            sourceUrl = new URL(source);
        } catch (MalformedURLException e) {
            throw new DataSourceParsingException("Bad source string given.", e);
        }

        DataSource output;
        switch (sourceUrl.getProtocol()) {
            case "file":
                output = new FileDataSource(sourceUrl);
                break;
            default:
                throw new DataSourceParsingException("Unsupported source type given.");
        }

        return output;
    }

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
    public abstract byte[] readDataIn() throws ManagerIOException;
    /**
     * Gets the output stream for saving a TimeManager that must be closed.
     *
     * @return The output stream for saving a TimeManager
     */
    public void writeDataOut(byte[] bytes) throws ManagerIOException {
        if (this.isReadOnly()) {
            throw new ManagerIOReadOnlyException();
        }
    }

    /**
     * Determines if the data source is read only or not.
     *
     * @return If the data source is read only or not
     */
    public abstract boolean isReadOnly();
}
