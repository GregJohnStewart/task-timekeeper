package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceReadOnlyException;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceWriteException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileDataSourceTest extends DataSourceTest<FileDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDataSourceTest.class);

    private final File testFileFolder =
            new File(
                    FileDataSourceTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/")
                            .getFile());
    private final File testFile = new File(testFileFolder.getPath() + "/temp.tks");

    public FileDataSourceTest() {
        LOGGER.debug("Test file location: {}", testFile);
        this.testSource = new FileDataSource(testFile);
    }

    @After
    public void cleanup() {
        this.testFileFolder.setWritable(true);
        if (testFile.exists() && testFile.isFile()) {
            testFile.setWritable(true);
            testFile.delete();
        }
    }

    private void createFile() throws IOException {
        try {
            if (!this.testFile.createNewFile()) {
                throw new RuntimeException("Test file could NOT be created. (" + this.testFile.getPath() + ")");
            }
        }catch (IOException e){
            LOGGER.error("Test file could not be created. File: '{}' ", this.testFile.getPath(), e);
            throw e;
        }
    }

    @Test
    public void altConstructorTest() throws MalformedURLException {
        this.testSource = new FileDataSource(new URL("file://" + this.testFile.getPath()));
        this.testSource = new FileDataSource(this.testFile.getPath());
    }

    // <editor-fold desc="Ensure read/write tests">
    @Test
    public void ensureReadWriteCapableTest() throws IOException {
        this.createFile();
        this.testSource.ensureReadWriteCapable();
    }

    @Test(expected = ManagerIOException.class)
    public void ensureReadWriteCapableActuallyADirTest() throws IOException {
        this.testSource = new FileDataSource(testFileFolder);
        this.testSource.ensureReadWriteCapable();
    }

    @Test(expected = DataSourceWriteException.class)
    public void ensureReadWriteCapableReadOnlyDirTest() throws IOException {
        testFileFolder.setWritable(false);
        this.testSource.ensureReadWriteCapable();
    }

    @Test
    public void ensureReadWriteCapableCreateNewFileTest() throws IOException {
        this.testSource.ensureReadWriteCapable();
    }

    @Test(expected = ManagerIOException.class)
    public void ensureReadWriteCapableNoCreateNewFileTest() throws IOException {
        this.testSource.ensureReadWriteCapable(false);
    }

    @Test(expected = DataSourceReadException.class)
    public void ensureReadWriteCapableCantReadTest() throws IOException {
        this.createFile();
        testFile.setReadable(false);
        this.testSource.ensureReadWriteCapable();
    }

    @Test(expected = DataSourceReadOnlyException.class)
    public void ensureReadWriteCapableCantWriteTest() throws IOException {
        this.createFile();
        testFile.setWritable(false);
        this.testSource.ensureReadWriteCapable();
    }
    // </editor-fold>

    @Test
    public void readOnlyTest() throws IOException {
        this.createFile();

        assertFalse(this.testSource.isReadOnly());

        this.testFile.setWritable(false);
        assertTrue(this.testSource.isReadOnly());
    }

    @Test
    public void readWriteTest() throws IOException {
        this.testSource.ensureReadWriteCapable(true);

        this.testSource.writeDataOut(this.testData);
        assertArrayEquals(this.testData, this.testSource.readDataIn());
    }

    @Test(expected = DataSourceReadException.class)
    public void readNoFileTest() throws IOException {
        this.testSource.readDataIn();
    }

    @Test
    public void writeNoFileTest() throws IOException {
        this.testSource.writeDataOut(this.testData);
        assertArrayEquals(this.testData, this.testSource.readDataIn());
    }

    @Test(expected = DataSourceWriteException.class)
    public void writeReadOnlyTest() throws IOException {
        this.createFile();
        this.testFile.setWritable(false);

        this.testSource.writeDataOut(this.testData);
    }
}
