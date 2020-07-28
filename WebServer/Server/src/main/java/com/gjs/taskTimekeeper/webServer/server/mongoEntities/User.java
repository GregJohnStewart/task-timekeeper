package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserInfo;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserLevel;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.notification.NotificationSettings;
import io.quarkus.mongodb.panache.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "Users")
@Slf4j
public class User extends OurMongoEntity {
	private String username;
	
	private String hashedPass;
	private String passResetTokenHash = null;
	
	private String email;
	private boolean emailValidated = false;
	private Date lastEmailValidated = null;
	private String emailValidationTokenHash = null;
	private String newEmail = null;
	
	private boolean approvedUser = false;
	private UserLevel level = UserLevel.REGULAR;
	private boolean locked = false;
	private String lockReason;
	private NotificationSettings notificationSettings;
	
	private Date joinDateTime = new Date();
	private Date noLoginsBefore = new Date();
	private Date lastLogin;
	private Long numLogins = 0L;
	private List<Date> lastHourLoginAttempts = new ArrayList<>();
	
	/**
	 * @param email The email of the user to find
	 * @return The user with the email given.
	 * @throws EntityNotFoundException if no user with the email was found.
	 */
	public static User findByEmail(String email) throws EntityNotFoundException {
		List<User> users = User.list("email", email);
		
		if(users.isEmpty()) {
			throw new EntityNotFoundException("No user with that email found.");
		}
		return users.get(0);
	}
	
	/**
	 * @param username The username of the user to find.
	 * @return The user with the given username.
	 * @throws EntityNotFoundException if no user with the username was found.
	 */
	public static User findByUsername(String username) throws EntityNotFoundException {
		List<User> users = User.list("username", username);
		
		if(users.isEmpty()) {
			throw new EntityNotFoundException("No user with that username found.");
		}
		return users.get(0);
	}
	
	/**
	 * Finds a user by either a username or email.
	 *
	 * @param emailUsername The password or email.
	 * @return the user with the email or username given.
	 * @throws EntityNotFoundException If a user was not found
	 */
	public static User findByEmailOrUsername(String emailUsername) throws EntityNotFoundException {
		try {
			return User.findByEmail(emailUsername);
		} catch(EntityNotFoundException e) {
		}
		try {
			return User.findByUsername(emailUsername);
		} catch(EntityNotFoundException e) {
		}
		throw new EntityNotFoundException("No user with given username or email found.");
	}
	
	/**
	 * Gets the user info about the user, a subset of the whole user object.
	 *
	 * @return the user info about the user, a subset of the whole user object.
	 */
	public UserInfo toUserInfo() {
		return new UserInfo(
			this.id.toHexString(),
			this.getUsername(),
			this.getEmail(),
			this.isEmailValidated(),
			this.isApprovedUser(),
			this.getLevel(),
			this.isLocked(),
			this.getLockReason(),
			this.getJoinDateTime(),
			this.getLastLogin()
		);
	}
}
