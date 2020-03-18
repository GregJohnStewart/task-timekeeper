package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.GroupMembership;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.NotificationSettings;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.UserLevel;
import io.quarkus.mongodb.panache.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection="Users")
public class User extends OurMongoEntity {
	private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

	private String username;
	private String hashedPass;
	private String email;
	private boolean emailValidated;
	private String emailValidationToken;
	private boolean approvedUser;
	private UserLevel level;
	private Date joinDateTime;
	private Date lastLogin;
	private NotificationSettings notificationSettings;
	private List<GroupMembership> memberships = new ArrayList<>();

	/**
	 * TODO:: test
	 * @param email The email of the user to find
	 * @return The user with the email given.
	 * @throws EntityNotFoundException if no user with the email was found.
	 */
	public static User findByEmail(String email) throws EntityNotFoundException {
		List<User> users = User.list("email", email);

		if(users.isEmpty()){
			throw new EntityNotFoundException("No user with that email found.");
		}
		return users.get(0);
	}

	/**
	 * TODO:: test
	 * @param username The username of the user to find.
	 * @return The user with the given username.
	 * @throws EntityNotFoundException if no user with the username was found.
	 */
	public static User findByUsername(String username) throws EntityNotFoundException {
		List<User> users = User.list("username", username);

		if(users.isEmpty()){
			throw new EntityNotFoundException("No user with that username found.");
		}
		return users.get(0);
	}

	/**
	 * Finds a user by either a username or email.
	 * @param emailUsername The password or email.
	 * @return the user with the email or username given.
	 * @throws EntityNotFoundException If a user was not found
	 */
	public static User findByEmailOrUsername(String emailUsername) throws EntityNotFoundException {
		try{
			return User.findByEmail(emailUsername);
		}catch (EntityNotFoundException e){
		}
		try{
			return User.findByUsername(emailUsername);
		}catch (EntityNotFoundException e){
		}
		throw new EntityNotFoundException("No user with given username or email found.");
	}
}
