package com.gjs.taskTimekeeper.webServer.server.mongoEntities;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.GroupMembership;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.NotificationSettings;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.UserLevel;
import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection="Users")
public class User extends PanacheMongoEntity {
	private String username;
	private String hashedPass;
	private String email;
	private boolean emailValidated;
	private String emailValidationToken;
	private boolean approvedUser;
	private UserLevel level;
	private ZonedDateTime joinDateTime;
	private ZonedDateTime lastLogin;
	private NotificationSettings notificationSettings;
	private List<GroupMembership> memberships = new ArrayList<>();

	/**
	 * TODO:: test
	 * @param email The email of the user to find
	 * @return The user with the email given.
	 * @throws EntityNotFoundException if no user with the email was found.
	 */
	public User getUserWithEmail(String email) throws EntityNotFoundException {
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
	public User getUserWithUsername(String username) throws EntityNotFoundException {
		List<User> users = User.list("username", username);

		if(users.isEmpty()){
			throw new EntityNotFoundException("No user with that username found.");
		}
		return users.get(0);
	}

}
