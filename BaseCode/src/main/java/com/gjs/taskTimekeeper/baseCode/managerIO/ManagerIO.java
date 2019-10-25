package com.gjs.taskTimekeeper.baseCode.managerIO;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSource;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOReadException;
import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.baseCode.utils.Outputter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerIO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerIO.class);
    private static final ObjectMapper TIME_MANAGER_MAPPER =
            ObjectMapperUtilities.getTimeManagerObjectMapper();

    private Outputter outputter = new Outputter(); // TODO:: use
    private DataSource dataSource;

    public TimeManager getManager(boolean createIfEmpty) throws ManagerIOException {
        LOGGER.info("Reading in a TimeManager.");
        this.dataSource.ensureReadWriteCapable();
        TimeManager manager = null;

        try {
            byte[] bytes = this.dataSource.readDataIn();
            LOGGER.debug("TimeManager serialized byte length: {}", bytes.length);
            if (bytes.length == 0) {
                LOGGER.warn("Data read in was empty.");
                if (createIfEmpty) {
                    this.saveManager(new TimeManager(), true);
                    return this.getManager(false);
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
        return manager;
    }

    public void saveManager(TimeManager manager, boolean useCompression) throws ManagerIOException {
        LOGGER.info("Writing out a TimeManager.");
        this.dataSource.ensureReadWriteCapable();

        try {
            byte[] bytes = TIME_MANAGER_MAPPER.writeValueAsBytes(manager);
            if (useCompression) {
                bytes = DataCompressor.compress(bytes);
            }
            this.dataSource.writeDataOut(bytes);
        } catch (ManagerIOException e) {
            LOGGER.error("There was a problem writing out the time manager.", e);
            throw e;
        } catch (JsonProcessingException e) {
            LOGGER.error("There was a json mapping error in serializing in the manager: ", e);
            throw new ManagerIOReadException(e);
        }

        // TODO
    }
}
