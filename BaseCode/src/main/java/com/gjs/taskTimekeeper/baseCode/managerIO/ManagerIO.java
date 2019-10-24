package com.gjs.taskTimekeeper.baseCode.managerIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSource;
import com.gjs.taskTimekeeper.baseCode.managerIO.exception.ManagerIOException;
import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.baseCode.utils.Outputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagerIO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerIO.class);
    private static final ObjectMapper TIME_MANAGER_MAPPER =
            ObjectMapperUtilities.getTimeManagerObjectMapper();

    private Outputter outputter = new Outputter();
    private DataSource dataSource;

    public TimeManager getManager() throws ManagerIOException {
        LOGGER.info("Reading in a TimeManager.");
        // TODO
        return null;
    }

    public void saveManager(TimeManager manager) throws ManagerIOException {
        LOGGER.info("Writing out a TimeManager.");
        // TODO
    }
}
