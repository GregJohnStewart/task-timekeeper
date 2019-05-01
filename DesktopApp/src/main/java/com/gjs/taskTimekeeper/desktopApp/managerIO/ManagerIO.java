package com.gjs.taskTimekeeper.desktopApp.managerIO;

import com.gjs.taskTimekeeper.backend.TimeManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ManagerIO {

	public abstract TimeManager load();
	public abstract void save(TimeManager manager);

	protected static TimeManager fromInputStream(InputStream is) throws IOException {
		return TimeManager.MAPPER.readValue(is, TimeManager.class);
	}

	protected static void toOutputStream(OutputStream os, TimeManager manager) throws IOException {
		TimeManager.MAPPER.writeValue(os, manager);
	}

	public static ManagerIO getProperIO(){
		//TODO:: get proper IO based on config
		return new LocalFile();
	}

	/**
	 * Loads the time manager data from the configured location.
	 * @return
	 */
	public static TimeManager loadTimeManager(){
		return getProperIO().load();
	}

	public static void saveTimeManager(TimeManager manager){
		getProperIO().save(manager);
	}
}
