package com.gjs.taskTimekeeper.baseCode.core.utils;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Default Jackson ObjectMapper utilities for serializing Timekeeper objects.
 */
public class ObjectMapperUtilities {
	private static final ObjectMapper DEFAULT_MAPPER;
	private static final Collection<Module> EXTRA_MODULES = new ArrayList<>(1);
	
	static {
		EXTRA_MODULES.add(new JavaTimeModule());
		DEFAULT_MAPPER = getTimeManagerObjectMapper();
	}
	
	/**
	 * Gets the default ObjectMapper.
	 *
	 * @return The default ObjectMapper
	 */
	public static ObjectMapper getDefaultMapper() {
		return DEFAULT_MAPPER;
	}
	
	/**
	 * Sets up a mapper given to handle serializing Timekeeper objects. Modifies the object given
	 * and returns the same object for flexibility of implementation.
	 *
	 * @param mapper The mapper to set up.
	 * @return The modified mapper object given.
	 */
	public static ObjectMapper setupObjectMapper(ObjectMapper mapper) {
		for(Module module : EXTRA_MODULES) {
			mapper.registerModule(module);
		}
		return mapper;
	}
	
	/**
	 * Gets a brand new object mapper setup for serializing Timekeeper objects
	 *
	 * @return a brand new object mapper setup for serializing Timekeeper objects
	 */
	public static ObjectMapper getTimeManagerObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		setupObjectMapper(mapper);
		return mapper;
	}
}
