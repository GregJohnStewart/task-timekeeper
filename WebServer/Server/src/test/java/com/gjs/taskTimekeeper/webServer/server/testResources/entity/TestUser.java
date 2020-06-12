package com.gjs.taskTimekeeper.webServer.server.testResources.entity;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class TestUser {
	@Getter(AccessLevel.PROTECTED)
	private final UserUtils userUtils;
	private String email;
	private String username;
	private String plainPassword;
	
	public User getUserObj() {
		if(persisted()) {
			return User.findByEmail(email);
		}
		return userUtils.setupNewUser(this, false);
	}
	
	public boolean persisted() {
		try {
			User.findByEmail(email);
			return true;
		} catch(EntityNotFoundException e) {
			return false;
		}
	}
}
