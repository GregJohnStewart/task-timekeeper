package com.gjs.taskTimekeeper.webServer.webLibrary.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
	//TODO:: add id
	private String username;
	private String email;
	private boolean emailValidated;
	private boolean approvedUser;
	private UserLevel level;
	private boolean locked;
	private String lockReason;
	private Date joinDateTime;
	private Date lastLogin;
}
