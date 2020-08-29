package com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojos;

import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginAuth {
	
	private String hashedPass;
	private String passResetTokenHash = null;
	
	private boolean emailValidated = false;
	private Date lastEmailValidated = null;
	private String emailValidationTokenHash = null;
	private String newEmail = null;
	
	private boolean approvedUser = false;
	private UserLevel level = UserLevel.REGULAR;
	private boolean locked = false;
	private String lockReason;
	private Date noLoginsBefore = new Date();
	private Date lastLogin;
	private Long numLogins = 0L;
	private List<Date> lastHourLoginAttempts = new ArrayList<>();
	
	@BsonIgnore
	public int getNumLastHourLoginAttempts() {
		return lastHourLoginAttempts.size();
	}
}
