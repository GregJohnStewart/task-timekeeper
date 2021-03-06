package com.gjs.taskTimekeeper.webServer.webLibrary.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenCheckResponse {
	private boolean hadToken = false;
	private boolean tokenSecure = false;
	private boolean expired = true;
	private Date expirationDate = null;
}
