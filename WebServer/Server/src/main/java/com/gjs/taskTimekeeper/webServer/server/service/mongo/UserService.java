package com.gjs.taskTimekeeper.webServer.server.service.mongo;

import com.gjs.taskTimekeeper.webServer.server.pojo.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService extends MongoService<User> {
	public UserService() {
		super(null, "users", User.class);
	}

	public User getOneByName(String name){
		return this.getOneByField("name", name);
	}

	public User getOneByEmail(String email){
		return this.getOneByField("email", email);
	}

}
