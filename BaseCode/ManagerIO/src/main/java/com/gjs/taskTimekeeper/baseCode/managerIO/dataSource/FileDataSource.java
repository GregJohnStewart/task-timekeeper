package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceNotFoundException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadOnlyException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Data source for a file on the local filesystem.
 *
 * <p>TODO:: double check exceptions
 */
public class FileDataSource extends DataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDataSource.class);

    private final File file;

    public FileDataSource(File file) {
        this.file = file;
    }

    public FileDataSource(URL url) {
        this(new File(url.getPath()));
    }

    public FileDataSource(String location) {
        this(new File(location));
    }

    /**
     * Ensures that the file exists.
     *
     * @param createIfNotExist Flag to create the file if it does not exist.
     * @throws DataSourceException If the file given is actually a directory.
     * @throws DataSourceWriteException If the source could not be written to.
     * @throws DataSourceNotFoundException If the source file could not be found and not set to
     *     create it.
     * @throws DataSourceReadException If the source could not be read
     * @throws DataSourceReadOnlyException If the file is readonly.
     */
    public void ensureReadWriteCapable(boolean createIfNotExist)
            throws DataSourceException, DataSourceWriteException, DataSourceNotFoundException,
                    DataSourceReadException, DataSourceReadOnlyException {
        if (this.file.isDirectory()) {
            throw new DataSourceException("File given is actually a directory.");
        }

        if (!this.file.exists() && createIfNotExist) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                throw new DataSourceWriteException(
                        "Could not create new empty file, file does not exist.", e);
            }
        } else if (!this.file.exists()) {
            throw new DataSourceNotFoundException("File does not exist.");
        }
        if (!this.file.canRead()) {
            throw new DataSourceReadException("File cannot be read.");
        }
        if (!this.file.canWrite()) {
            throw new DataSourceReadOnlyException("File is read only.");
        }
    }

    /**
     * Ensures that the file exists.
     *
     * @throws DataSourceException If the file given is actually a directory.
     * @throws DataSourceWriteException If the source could not be written to.
     * @throws DataSourceReadException If the source could not be read
     * @throws DataSourceReadOnlyException If the file is readonly.
     */
    @Override
    public void ensureReadWriteCapable()
            throws DataSourceException, DataSourceWriteException, DataSourceReadException,
                    DataSourceReadOnlyException {
        this.ensureReadWriteCapable(true);
    }

    @Override
    public byte[] readDataIn() throws DataSourceReadException {
        try (FileInputStream is = new FileInputStream(this.file); ) {
            byte[] output = new byte[is.available()];
            is.read(output);
            return output;
        } catch (IOException e) {
            LOGGER.error("Error reading in file: ", e);
            throw new DataSourceReadException(e);
        }
    }

    @Override
    public void writeDataOut(byte[] bytes) throws DataSourceWriteException {
        try (FileOutputStream outputStream = new FileOutputStream(this.file); ) {
            outputStream.write(bytes);
        } catch (IOException e) {
            LOGGER.error("Error writing out file: ", e);
            throw new DataSourceWriteException(e);
        }
        LOGGER.info("File written successfully.");
    }

    @Override
    public boolean isReadOnly() {
        return !this.file.canWrite();
    }
}
