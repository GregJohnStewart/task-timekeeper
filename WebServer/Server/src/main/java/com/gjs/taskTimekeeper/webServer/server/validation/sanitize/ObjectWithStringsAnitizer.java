package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Field;

@ApplicationScoped
@Slf4j
public class ObjectWithStringsAnitizer extends Anitizer<Object> {
	
	@Inject
	HTMLAnitizer htmlAnitizer;
	
	@Override
	public Object anitize(Object object, AnitizeOp operation) {
		if(object == null) {
			return object;
		}
		
		if(object instanceof String) {
			object = this.htmlAnitizer.anitize((String)object, operation);
		} else {
			Field[] fields = object.getClass().getDeclaredFields();
			for(Field field : fields) {
				Class t = field.getType();
				if(t == String.class) {
					field.setAccessible(true);
					String orig = null;
					
					try {
						orig = (String)field.get(object);
					} catch(IllegalAccessException e) {
						log.error("Failed to get field {} in action request config.", field, e);
						continue;
					}
					try {
						field.set(object, this.htmlAnitizer.anitize(orig, operation));
					} catch(IllegalAccessException | IllegalArgumentException e) {
						log.error("Failed to set field {} in action request config.", field, e);
						continue;
					}
				}
			}
		}
		
		return object;
	}
}
