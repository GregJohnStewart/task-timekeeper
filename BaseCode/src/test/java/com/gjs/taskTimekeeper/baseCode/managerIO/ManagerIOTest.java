package com.gjs.taskTimekeeper.baseCode.managerIO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.ByteArrayDataSource;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadOnlyException;
import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.baseCode.utils.Outputter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerIOTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerIOTest.class);
    private static final ObjectMapper TIME_MANAGER_MAPPER =
            ObjectMapperUtilities.getDefaultMapper();
    private static final File TEST_DATA_LOCATION =
            new File(
                    ManagerIOTest.class
                            .getClassLoader()
                            .getResource("testTimeManagerData/fully_populated.json")
                            .getFile());

    private byte[] populatedManagerData;
    private TimeManager populatedManager;
    private ByteArrayDataSource source = new ByteArrayDataSource();
    private ManagerIO io = new ManagerIO(this.source);

    {
        try (FileInputStream is = new FileInputStream(TEST_DATA_LOCATION)) {
            populatedManagerData = new byte[is.available()];
            is.read(populatedManagerData);

            populatedManager =
                    TIME_MANAGER_MAPPER.readValue(populatedManagerData, TimeManager.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOtherConstructor() {
        new ManagerIO(new ByteArrayDataSource(), new Outputter());
    }

    // <editor-fold desc="Saving Tests">
    @Test
    public void saveTimeManagerWithoutCompression() throws IOException {
        io.saveManager(this.populatedManager, false);
        LOGGER.info(
                "Length of uncompressed time manager data: {} bytes.", source.getBuffer().length);
        TimeManager result = TIME_MANAGER_MAPPER.readValue(source.getBuffer(), TimeManager.class);

        assertEquals(this.populatedManager, result);
    }

    @Test
    public void saveTimeManagerWithCompression() throws IOException {
        io.saveManager(this.populatedManager, true);
        LOGGER.info("Length of compressed time manager data: {} bytes.", source.getBuffer().length);
        TimeManager result =
                TIME_MANAGER_MAPPER.readValue(
                        DataCompressor.decompress(source.getBuffer()), TimeManager.class);

        assertEquals(this.populatedManager, result);
    }

    @Test(expected = ManagerIOReadOnlyException.class)
    public void saveTimeManagerReadOnly() throws IOException {
        this.source.setReadOnly(true);
        io.saveManager(this.populatedManager, true);
    }
    // </editor-fold>
    // <editor-fold desc="Loading Tests">
    @Test
    public void loadPopulatedTimeManager() throws IOException {
        this.source.writeDataOut(this.populatedManagerData);

        TimeManager retrievedManager = io.loadManager(false);

        assertEquals(this.populatedManager, retrievedManager);
    }

    @Test
    public void loadPopulatedTimeManagerReadonly() throws IOException {
        this.source.writeDataOut(this.populatedManagerData);
        this.source.setReadOnly(true);

        TimeManager retrievedManager = io.loadManager(false);

        assertEquals(this.populatedManager, retrievedManager);
    }

    @Test(expected = ManagerIOReadException.class)
    public void loadEmptyTimeManagerNoCreate() throws IOException {
        io.loadManager(false);
    }

    @Test
    public void loadEmptyTimeManager() throws IOException {
        TimeManager retrievedManager = io.loadManager(true);

        assertNotEquals(this.populatedManager, retrievedManager);
        assertTrue(retrievedManager.getTasks().isEmpty());
        assertTrue(retrievedManager.getWorkPeriods().isEmpty());

        TimeManager resultFromSource =
                TIME_MANAGER_MAPPER.readValue(
                        DataCompressor.decompress(source.getBuffer()), TimeManager.class);
        assertEquals(retrievedManager, resultFromSource);
    }

    @Test(expected = ManagerIOReadException.class)
    public void loadBadTimeManager() throws IOException {
        this.source.writeDataOut(this.populatedManagerData);
        byte[] sourceBuff = this.source.getBuffer();
        sourceBuff[sourceBuff.length - 1] = 4;
        sourceBuff[sourceBuff.length - 2] = 4;
        sourceBuff[sourceBuff.length - 3] = 4;
        io.loadManager(true);
    }
    // </editor-fold>
}
