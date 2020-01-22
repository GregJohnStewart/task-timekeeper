package com.gjs.taskTimekeeper.webServer.server.pojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class User extends MongoObject {
	private String username;
	private String hashedPass;
	private String email;
	private ZonedDateTime joinDateTime;
	private ZonedDateTime lastLogin;
	private NotificationSettings notificationSettings;
	private List<GroupMembership> memberships = new ArrayList<>();

	public static void configureObjectMapper(ObjectMapper mapper){
		mapper.registerModule(new JavaTimeModule());
		NotificationSettings.configureObjectMapper(mapper);
	}

	public static ObjectMapper getObjectMapper(){
		ObjectMapper mapper = new ObjectMapper();
		configureObjectMapper(new ObjectMapper());
		return mapper;
	}
}
