package com.gjs.taskTimekeeper.webServer.webLibrary.user.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatePasswordRequest {
	private String oldPlainPassword;
	private String newPlainPassword;
}
