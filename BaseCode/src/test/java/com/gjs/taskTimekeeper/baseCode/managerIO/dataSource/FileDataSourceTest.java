package com.gjs.taskTimekeeper.baseCode.managerIO.dataSource;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOWriteException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (!this.testFile.createNewFile()) {
            throw new RuntimeException("Test file could NOT be created.");
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

    @Test(expected = ManagerIOWriteException.class)
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

    @Test(expected = ManagerIOReadException.class)
    public void ensureReadWriteCapableCantReadTest() throws IOException {
        this.createFile();
        testFile.setReadable(false);
        this.testSource.ensureReadWriteCapable();
    }

    @Test(expected = ManagerIOWriteException.class)
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

    @Test(expected = ManagerIOReadException.class)
    public void readNoFileTest() throws IOException {
        this.testSource.readDataIn();
    }

    @Test
    public void writeNoFileTest() throws IOException {
        this.testSource.writeDataOut(this.testData);
        assertArrayEquals(this.testData, this.testSource.readDataIn());
    }

    @Test(expected = ManagerIOWriteException.class)
    public void writeReadOnlyTest() throws IOException {
        this.createFile();
        this.testFile.setWritable(false);

        this.testSource.writeDataOut(this.testData);
    }
}
