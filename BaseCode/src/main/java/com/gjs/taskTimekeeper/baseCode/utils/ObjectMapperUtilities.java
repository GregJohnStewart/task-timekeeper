package com.gjs.taskTimekeeper.baseCode.utils;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Collection;
import java.util.List;

public class ObjectMapperUtilities {
	private static final ObjectMapper DEFAULT_MAPPER;
	private static final Collection<Module> EXTRA_MODULES = List.of(new JavaTimeModule());

	static {
		DEFAULT_MAPPER = getTimeManagerObjectMapper();
	}

	public static ObjectMapper getDefaultMapper(){
		return DEFAULT_MAPPER;
	}

	public static ObjectMapper setupObjectMapper(ObjectMapper mapper){
		for(Module module : EXTRA_MODULES){
			mapper.registerModule(module);
		}
		return mapper;
	}

	public static ObjectMapper getTimeManagerObjectMapper(){
		ObjectMapper mapper = new ObjectMapper();
		setupObjectMapper(mapper);
		return mapper;
	}
}
