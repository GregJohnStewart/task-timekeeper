package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.action.TimeManagerActionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Field;

/**
 * TODO:: test
 */
@ApplicationScoped
public class TimeManagerActionDeSanitizer implements DeSanitizer<TimeManagerActionRequest> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeManagerActionDeSanitizer.class);
	
	@Inject
	HTMLAnitizer htmlAnitizer;
	
	private void deSanitizeConfig(ActionConfig config) {
		if(config == null) {
			return;
		}
		
		Field[] fields = config.getClass().getDeclaredFields();
		for(Field field : fields) {
			Class t = field.getType();
			if(t == String.class) {
				field.setAccessible(true);
				String orig = null;
				
				try {
					orig = (String)field.get(config);
				} catch(IllegalAccessException e) {
					LOGGER.error("Failed to get field {} in action request config.", field, e);
					continue;
				}
				try {
					field.set(config, this.htmlAnitizer.deSanitize(orig));
				} catch(IllegalAccessException | IllegalArgumentException e) {
					LOGGER.error("Failed to set field {} in action request config.", field, e);
					continue;
				}
			}
		}
	}
	
	@Override
	public TimeManagerActionRequest deSanitize(TimeManagerActionRequest request) {
		if(request == null) {
			return null;
		}
		deSanitizeConfig(request.getActionConfig());
		
		return request;
	}
}
