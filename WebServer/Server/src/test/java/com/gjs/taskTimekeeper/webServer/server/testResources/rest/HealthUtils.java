package com.gjs.taskTimekeeper.webServer.server.testResources.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class HealthUtils {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public static JsonNode assertHealthCheckContains(String checkName, JsonNode checkResponse) throws IOException {
		for(JsonNode curCheck : checkResponse.get("checks")) {
			if(checkName.equals(curCheck.get("name").asText())) {
				return curCheck;
			}
		}
		fail();
		return null;
	}
}
