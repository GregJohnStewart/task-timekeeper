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
		for(Field f : fields) {
			Class t = f.getType();
			if(t == String.class) {
				try {
					f.set(f, this.htmlAnitizer.deSanitize((String)f.get(config)));
				} catch(IllegalAccessException e) {
					LOGGER.error("Failed to get/set field {} in action request config.", f);
				}
			}
		}
	}
	
	@Override
	public TimeManagerActionRequest deSanitize(TimeManagerActionRequest request) {
		deSanitizeConfig(request.getActionConfig());
		
		return request;
	}
}
