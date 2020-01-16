package com.gjs.taskTimekeeper.webServer.server.service.mongo;

import com.gjs.taskTimekeeper.webServer.server.pojo.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService extends MongoService<User> {
	public UserService() {
		super("task-timekeeper", "users", User.class);
	}
}
