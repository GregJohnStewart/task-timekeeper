package com.gjs.taskTimekeeper.desktopApp.managerIO;

import com.gjs.taskTimekeeper.baseCode.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.utils.ObjectMapperUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ManagerIO {

    public abstract TimeManager load();

    public abstract void save(TimeManager manager);

    protected static TimeManager fromInputStream(InputStream is) throws IOException {
        return ObjectMapperUtilities.getDefaultMapper().readValue(is, TimeManager.class);
    }

    protected static void toOutputStream(OutputStream os, TimeManager manager) throws IOException {
        ObjectMapperUtilities.getDefaultMapper().writeValue(os, manager);
    }

    public static ManagerIO getProperIO() {
        // TODO:: get proper IO based on config
        return new LocalFile();
    }

    /**
     * Loads the time manager data from the configured location.
     *
     * @return
     */
    public static TimeManager loadTimeManager() {
        return getProperIO().load();
    }

    public static void saveTimeManager(TimeManager manager) {
        getProperIO().save(manager);
    }
}
