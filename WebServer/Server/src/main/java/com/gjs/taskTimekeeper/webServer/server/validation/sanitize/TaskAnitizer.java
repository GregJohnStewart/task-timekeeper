package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TaskAnitizer extends Anitizer<Task> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAnitizer.class);
    
    @Inject
    HTMLAnitizer htmlAnitizer;
    
    @Inject
    StringMapAnitizer stringMapAnitizer;
    
    @Override
    public Task anitize(Task task, AnitizeOp op) {
        if(task == null) {
            return task;
        }
        task.setName(new Name(this.htmlAnitizer.anitize(task.getName().getName(), op)));
        task.setAttributes(this.stringMapAnitizer.anitize(task.getAttributes(), op));
        
        return task;
    }
}
