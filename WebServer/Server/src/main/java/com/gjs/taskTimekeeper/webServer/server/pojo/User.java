package com.gjs.taskTimekeeper.webServer.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends MongoObject {
	private String username;
	private String hashedPass;
	private String email;
	private ZonedDateTime joinDateTime;
	private ZonedDateTime lastLogin;
	private NotificationSettings notificationSettings;
	private List<GroupMembership> memberships = new ArrayList<>();
}
