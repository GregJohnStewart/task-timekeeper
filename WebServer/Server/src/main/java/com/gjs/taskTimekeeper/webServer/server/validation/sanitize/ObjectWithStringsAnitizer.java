package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Field;

@ApplicationScoped
public class ObjectWithStringsAnitizer extends Anitizer<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectWithStringsAnitizer.class);
    
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
                        LOGGER.error("Failed to get field {} in action request config.", field, e);
                        continue;
                    }
                    try {
                        field.set(object, this.htmlAnitizer.anitize(orig, operation));
                    } catch(IllegalAccessException | IllegalArgumentException e) {
                        LOGGER.error("Failed to set field {} in action request config.", field, e);
                        continue;
                    }
                }
            }
        }
        
        return object;
    }
    
    @Override
    public Object deSanitize(Object object) {
        return this.anitize(object, AnitizeOp.DESANITIZE);
    }
    
    @Override
    public Object sanitize(Object object) {
        return this.anitize(object, AnitizeOp.SANITIZE);
    }
}
