package com.gjs.taskTimekeeper.webServer.server.health;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.gjs.taskTimekeeper.webServer.server.health.DefaultKeysHealthCheck.CHECK_NAME;
import static com.gjs.taskTimekeeper.webServer.server.testResources.rest.HealthUtils.assertHealthCheckContains;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class DefaultKeysHealthCheckTest {
	@Test
	public void testHealthLiveEndpoint() throws IOException {
		ValidatableResponse response = given()
			.when().get("/health")
			.then()
			.statusCode(200);
		
		JsonNode responseNode = response.extract().as(JsonNode.class);
		
		assertHealthCheckContains(CHECK_NAME, responseNode);
	}
}
