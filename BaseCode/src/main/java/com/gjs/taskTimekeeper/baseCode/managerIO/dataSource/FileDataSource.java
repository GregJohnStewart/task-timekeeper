package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadOnlyException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOWriteException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void ensureReadWriteCapable(boolean createIfNotExist) throws ManagerIOException {
        if (this.file.isDirectory()) {
            throw new ManagerIOException("File given is actually a directory.");
        }

        if (!this.file.exists() && createIfNotExist) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                throw new ManagerIOWriteException(
                        "Could not create new empty file, file does not exist.", e);
            }
        } else if (!this.file.exists()) {
            throw new ManagerIOException("File does not exist.");
        }
        if (!this.file.canRead()) {
            throw new ManagerIOReadException("File cannot be read.");
        }
        if (!this.file.canWrite()) {
            throw new ManagerIOReadOnlyException("File is read only.");
        }
    }

    @Override
    public void ensureReadWriteCapable() throws ManagerIOException {
        this.ensureReadWriteCapable(true);
    }

    @Override
    public byte[] readDataIn() throws ManagerIOException {
        try (FileInputStream is = new FileInputStream(this.file); ) {
            byte[] output = new byte[is.available()];
            is.read(output);
            return output;
        } catch (IOException e) {
            LOGGER.error("Error reading in file: ", e);
            throw new ManagerIOReadException(e);
        }
    }

    @Override
    public void writeDataOut(byte[] bytes) throws ManagerIOException {
        try (FileOutputStream outputStream = new FileOutputStream(this.file); ) {
            outputStream.write(bytes);
        } catch (IOException e) {
            LOGGER.error("Error writing out file: ", e);
            throw new ManagerIOWriteException(e);
        }
        LOGGER.info("File written successfully.");
    }

    @Override
    public boolean isReadOnly() {
        return !this.file.canWrite();
    }
}
