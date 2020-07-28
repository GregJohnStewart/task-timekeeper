package com.gjs.taskTimekeeper.baseCode.managerIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.ByteArrayDataSource;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManagerIOTest {
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
        this.io.setManager(populatedManager.clone(), true);
    }

    // <editor-fold desc="Constructor Tests">
    @Test
    public void testManagerIoDataSource() {
        ManagerIO io = new ManagerIO(this.source);

        assertSame(this.source, io.getDataSource());
        assertEquals(this.populatedManager, io.getManager());

        assertFalse(io.isDifferentFromSource());
        assertFalse(io.isUnSaved(false));
    }

    @Test
    public void testManagerIoDataSourceOutputter() {
        Outputter outputter = new Outputter();
        ManagerIO io = new ManagerIO(this.source, outputter);

        assertSame(this.source, io.getDataSource());
        assertEquals(this.populatedManager, io.getManager());

        assertSame(outputter, io.getManager().getCrudOperator().getOutputter());
    }

    @Test
    public void testManagerIoDataSourceTimeManagerSave() {
        ManagerIO io = new ManagerIO(this.source, this.populatedManager.clone(), true);

        assertSame(this.source, io.getDataSource());
        assertEquals(this.populatedManager, io.getManager());
        assertEquals(this.populatedManager, io.loadManagerFromSource(false));
    }

    @Test
    public void testManagerIoDataSourceTimeManagerNoSave() {
        ManagerIO io = new ManagerIO(this.source, new TimeManager(), false);

        assertSame(this.source, io.getDataSource());
        assertEquals(new TimeManager(), io.getManager());
        assertEquals(this.populatedManager, io.loadManagerFromSource(false));
    }

    @Test
    public void testManagerIoDataSourceTimeManagerOutputter() {
        Outputter outputter = new Outputter();
        ManagerIO io = new ManagerIO(this.source, new TimeManager(), false, outputter);

        assertSame(this.source, io.getDataSource());
        assertEquals(new TimeManager(), io.getManager());
        assertEquals(this.populatedManager, io.loadManagerFromSource(false));
        assertSame(outputter, io.getManager().getCrudOperator().getOutputter());
    }
    // </editor-fold>
    // <editor-fold desc="Setter/ getter tests">
    @Test
    public void setOutputterTest() {
        Outputter outputter = new Outputter();

        this.io.setOutputter(outputter);

        assertSame(outputter, this.io.getManager().getCrudOperator().getOutputter());
    }

    @Test
    public void setNullOutputterTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.io.setOutputter(null);
        });
    }

    @Test
    public void setNullDataSource() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.io.setDataSource(null, false);
        });
    }

    @Test
    public void setDataSourceCheck() {
        ByteArrayDataSource ds = new ByteArrayDataSource(this.populatedManagerData);
        this.io.setManager(new TimeManager(), false);

        this.io.setDataSource(ds, true);

        assertTrue(this.io.isUnSaved(false));
        assertSame(ds, this.io.getDataSource());
    }

    @Test
    public void setDataSourceNoCheck() {
        ByteArrayDataSource ds = new ByteArrayDataSource(this.populatedManagerData);
        this.io.setManager(new TimeManager(), false);

        this.io.setDataSource(ds, false);

        assertFalse(this.io.isUnSaved(false));
        assertSame(ds, this.io.getDataSource());
    }

    @Test
    public void setNullTimeManager() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.io.setManager(null, true);
        });
    }

    @Test
    public void setTimeManagerSave() {
        TimeManager manager = new TimeManager();

        this.io.setManager(manager, true);

        assertSame(manager, this.io.getManager());
        assertEquals(manager, this.io.loadManagerFromSource(false));
    }

    @Test
    public void setTimeManagerNoSave() {
        TimeManager manager = new TimeManager();

        this.io.setManager(manager, false);

        assertSame(manager, this.io.getManager());
        assertNotEquals(manager, this.io.loadManagerFromSource(false));
    }

    @Test
    public void setUsesCompression() {
        this.io.setUseCompression(true);
        assertTrue(this.io.usesCompression());
        this.io.saveManager();
        assertTrue(DataCompressor.isDataCompressed(this.source.getBuffer()));

        this.io.setUseCompression(false);
        assertFalse(this.io.usesCompression());
        this.io.saveManager();
        assertFalse(DataCompressor.isDataCompressed(this.source.getBuffer()));
    }
    // </editor-fold>
    // <editor-fold desc="Loading/ state test">
    @Test
    public void isDifferentFromSourceBadSource() {
        this.source.writeDataOut(new byte[0]);
        Assertions.assertThrows(ManagerIOReadException.class, () -> {
            this.io.isDifferentFromSource();
        });
    }

    @Test
    public void isDifferentFromSource() {
        assertFalse(this.io.isDifferentFromSource());
        this.io.setManager(new TimeManager(), false);
        assertTrue(this.io.isDifferentFromSource());
    }

    @Test
    public void isUnsavedBadSource() {
        this.source.writeDataOut(new byte[0]);
        Assertions.assertThrows(ManagerIOReadException.class, () -> {
            this.io.isUnSaved(true);
        });
    }

    @Test
    public void isUnsavedSourceCheckSource() {
        assertFalse(this.io.isUnSaved(true));
        this.io.setManager(new TimeManager(), false);
        assertTrue(this.io.isUnSaved(true));
    }

    @Test
    public void isUnsavedSourceNoCheckSource() {
        assertFalse(this.io.isUnSaved(false));
        this.io.setManager(new TimeManager(), false);
        assertFalse(this.io.isUnSaved(false));
    }

    @Test
    public void loadPopulatedTimeManager() {
        this.source.writeDataOut(this.populatedManagerData);

        TimeManager retrievedManager = io.loadManagerFromSource(false);

        assertEquals(this.populatedManager, retrievedManager);
    }

    @Test
    public void loadPopulatedTimeManagerReadonly() throws IOException {
        this.source.writeDataOut(this.populatedManagerData);
        this.source.setReadOnly(true);

        TimeManager retrievedManager = io.loadManagerFromSource(false);

        assertEquals(this.populatedManager, retrievedManager);
    }

    @Test
    public void loadEmptyTimeManagerNoCreate() throws IOException {
        this.source.writeDataOut(new byte[0]);

        Assertions.assertThrows(ManagerIOReadException.class, () -> {
            io.loadManagerFromSource(false);
        });
    }

    @Test
    public void loadEmptyTimeManager() throws IOException {
        this.source.writeDataOut(new byte[0]);
        TimeManager retrievedManager = io.loadManagerFromSource(true);

        assertNotEquals(this.populatedManager, retrievedManager);
        assertTrue(retrievedManager.getTasks().isEmpty());
        assertTrue(retrievedManager.getWorkPeriods().isEmpty());

        TimeManager resultFromSource =
                TIME_MANAGER_MAPPER.readValue(
                        DataCompressor.decompress(source.getBuffer()), TimeManager.class);
        assertEquals(retrievedManager, resultFromSource);
    }

    @Test
    public void loadBadTimeManager() throws IOException {
        this.source.writeDataOut(this.populatedManagerData);
        byte[] sourceBuff = this.source.getBuffer();
        sourceBuff[sourceBuff.length - 1] = 4;
        sourceBuff[sourceBuff.length - 2] = 4;
        sourceBuff[sourceBuff.length - 3] = 4;

        Assertions.assertThrows(ManagerIOReadException.class, () -> {
            io.loadManager(true);
        });
    }
    // </editor-fold>
    // <editor-fold desc="Saving Tests">
    // </editor-fold>
}
