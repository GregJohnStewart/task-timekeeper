package com.gjs.taskTimekeeper.webServer.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements MongoObject {
	private ObjectId id;
	private String username;
	private String hashedPass;
	private NotificationSettings notificationSettings;
	private List<GroupMembership> memberships = new ArrayList<>();
}
