package com.gjs.taskTimekeeper.webServer.server.utils;


import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class UserUtils {
	
	@ConfigProperty(name = "user.login.loginAttemptsAllowed")
	int loginAttemptThreshold;
	
	//TODO:: test
	@BsonIgnore
	public boolean numLoginAttemptsDoesNotExceed(User user) {
		return user.getLoginAuth().getNumLastHourLoginAttempts() < this.loginAttemptThreshold;
	}
	
	//TODO:: test
	@BsonIgnore
	public boolean canLogin(User user) {
		return user.getLoginAuth().isApprovedUser() &&
			!user.getLoginAuth().isLocked() &&
			user.getLoginAuth().isEmailValidated() &&
			!this.numLoginAttemptsDoesNotExceed(user);
	}
}
