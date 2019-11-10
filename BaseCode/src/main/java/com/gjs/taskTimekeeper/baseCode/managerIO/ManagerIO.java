package com.gjs.taskTimekeeper.baseCode.managerIO;

import static com.gjs.taskTimekeeper.baseCode.utils.OutputLevel.DEFAULT;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSource;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadOnlyException;
import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.baseCode.utils.Outputter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to manage a time manager for a runner.
 *
 * <p>Use by calling on the result from {@link #getManager()} directly for reading, {@link
 * #doCrudAction(ActionConfig, boolean)}
 */
public class ManagerIO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerIO.class);
    private static final ObjectMapper TIME_MANAGER_MAPPER =
            ObjectMapperUtilities.getTimeManagerObjectMapper();

    private Outputter outputter = new Outputter();
    private DataSource dataSource;
    private TimeManager manager;
    private boolean unSaved = false;
    private boolean autoSave = false; // TODO:: implement setter/getter; test
    private boolean useCompression = true;

    // <editor-fold desc="Constructors">
    public ManagerIO(DataSource dataSource) {
        this.setDataSource(dataSource, false);
        this.loadManager(true);
    }

    public ManagerIO(DataSource dataSource, Outputter outputter) {
        this(dataSource);
        this.setOutputter(outputter);
    }

    public ManagerIO(DataSource dataSource, TimeManager manager, boolean save) {
        this.setDataSource(dataSource, false);
        this.setManager(manager, save);
    }

    public ManagerIO(
            DataSource dataSource, TimeManager manager, boolean save, Outputter outputter) {
        this(dataSource, manager, save);
        this.setOutputter(outputter);
    }
    // </editor-fold>

    // <editor-fold desc="Setters/ getters">

    public Outputter getOutputter() {
        return outputter;
    }

    public ManagerIO setOutputter(Outputter outputter) {
        if (outputter == null) {
            throw new IllegalArgumentException("Outputter cannot be null.");
        }
        this.outputter = outputter;
        this.getManager().getCrudOperator().setOutputter(this.outputter);
        return this;
    }

    public ManagerIO setDataSource(DataSource dataSource, boolean checkIfDifferent) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source cannot be null.");
        }
        this.dataSource = dataSource;
        if (checkIfDifferent) {
            this.unSaved = this.isDifferentFromSource();
        }
        return this;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public ManagerIO setManager(TimeManager manager, boolean save) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null.");
        }
        this.manager = manager;
        if (save) {
            this.saveManager();
        }
        return this;
    }

    public TimeManager getManager() {
        return manager;
    }

    public ManagerIO setUseCompression(boolean useCompression) {
        this.useCompression = useCompression;
        return this;
    }

    public boolean usesCompression() {
        return useCompression;
    }

    /**
     * Returns if the source is readOnly or not.
     *
     * @return if the source is readOnly or not.
     */
    public boolean sourceIsReadOnly() {
        return this.dataSource.isReadOnly();
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public ManagerIO setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
        return this;
    }

    // </editor-fold>
    // <editor-fold desc="loading/ change status">
    /**
     * Determines if the current manager held is different from the one saved at the source.
     *
     * @return If the current mannullager is the same as the one in the source.
     * @throws ManagerIOReadException If the read failed to do the check with.
     */
    public boolean isDifferentFromSource() throws ManagerIOReadException {
        try {
            return !this.manager.equals(this.loadManagerFromSource(false));
        } catch (Exception e) {
            throw new ManagerIOReadException(
                    "Unable to determine if current manager is different from the one saves at source.",
                    e);
        }
    }

    /**
     * Determines if the held time manager has been changed
     *
     * @param checkSource If this should read from source to see if the manager is different.
     * @return If the held time manager has been changed.
     */
    public boolean isUnSaved(boolean checkSource) {
        if (checkSource) {
            this.unSaved = this.isDifferentFromSource();
        }
        return unSaved;
    }

    /**
     * Loads the manager from source and just returns it.
     *
     * @param createIfEmpty True to create an empty manager if source empty.
     * @return The manager loaded from source.
     * @throws ManagerIOException
     */
    public TimeManager loadManagerFromSource(boolean createIfEmpty) throws ManagerIOException {
        LOGGER.info("Reading in a TimeManager.");
        try {
            this.dataSource.ensureReadWriteCapable();
        } catch (ManagerIOReadOnlyException e) {
            LOGGER.warn("Manager source is read only: ", e);
        }

        TimeManager manager;
        try {
            byte[] bytes = this.dataSource.readDataIn();
            LOGGER.debug("TimeManager serialized byte length: {}", bytes.length);
            if (bytes.length == 0) {
                LOGGER.warn("Data read in was empty.");
                if (createIfEmpty) {
                    this.setManager(new TimeManager(), true);
                    return this.loadManagerFromSource(false);
                } else {
                    LOGGER.error("Could not read data from source, was empty.");
                    throw new ManagerIOReadException("Could not read data from source, was empty.");
                }
            }
            bytes = DataCompressor.decompress(bytes);
            manager = TIME_MANAGER_MAPPER.readValue(bytes, TimeManager.class);
        } catch (ManagerIOException e) {
            LOGGER.error("There was a problem reading in the time manager.", e);
            throw e;
        } catch (JsonParseException e) {
            LOGGER.error("There was an error parsing the manager: ", e);
            throw new ManagerIOReadException(e);
        } catch (JsonMappingException e) {
            LOGGER.error("There was a json mapping error in reading in the manager: ", e);
            throw new ManagerIOReadException(e);
        } catch (IOException e) {
            LOGGER.error("There was a miscellaneous error in reading in the manager: ", e);
            throw new ManagerIOReadException(e);
        }
        LOGGER.info("Read in TimeManager");
        manager.getCrudOperator().setOutputter(outputter);
        return manager;
    }

    /**
     * Loads the manager from source and just returns it.
     *
     * @return The manager loaded from source.
     * @throws ManagerIOException If something happened when loading the manager.
     */
    public TimeManager loadManagerFromSource() throws ManagerIOException {
        return this.loadManagerFromSource(false);
    }

    /**
     * Loads the manager from the source, and sets it as the manager held.
     *
     * @param createIfEmpty If the source holds nothing,
     * @throws ManagerIOException If there was a problem reading in the manager.
     */
    public void loadManager(boolean createIfEmpty) throws ManagerIOException {
        this.setManager(loadManagerFromSource(createIfEmpty), false);
    }
    // </editor-fold>

    // <editor-fold desc="Saving">
    /**
     * Saves the time manager to the data source held.
     *
     * @throws ManagerIOException If the source is readonly, or anything else goes wrong with
     *     saving.
     */
    public void saveManager() throws ManagerIOException {
        LOGGER.info("Writing out a TimeManager.");
        this.outputter.normPrintln(DEFAULT, "Saving Time Manager...");
        this.dataSource.ensureReadWriteCapable();
        LOGGER.debug("Data source is writable");
        try {
            byte[] bytes = TIME_MANAGER_MAPPER.writeValueAsBytes(manager);
            if (this.usesCompression()) {
                bytes = DataCompressor.compress(bytes);
            }
            LOGGER.debug("Size of data to write out: {} bytes", bytes.length);
            this.dataSource.writeDataOut(bytes);
        } catch (ManagerIOException e) {
            LOGGER.error("There was a problem writing out the time manager.", e);
            throw e;
        } catch (JsonProcessingException e) {
            LOGGER.error("There was a json mapping error in serializing in the manager: ", e);
            throw new ManagerIOReadException(e);
        }
        LOGGER.info("Data written out.");
        this.outputter.normPrintln(DEFAULT, "Saved.");
        this.unSaved = false;
    }

    /**
     * Performs a crud action on the held time manager.
     *
     * @param config the action config to use
     * @return The result from performing the action
     */
    public boolean doCrudAction(ActionConfig config, boolean saveIfChanged)
            throws ManagerIOException {
        this.unSaved = this.unSaved | this.getManager().getCrudOperator().doObjAction(config);
        if (saveIfChanged && this.unSaved) {
            this.saveManager();
        }
        return this.unSaved;
    }

    /**
     * Performs a crud action on the held time manager.
     *
     * @param config
     * @return
     * @throws ManagerIOException
     */
    public boolean doCrudAction(ActionConfig config) throws ManagerIOException {
        return this.doCrudAction(config, this.autoSave);
    }
    // </editor-fold>
}
