package com.gjs.taskTimekeeper.webServer.server.endpoints.user.auth;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.TokenCheckResponse;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserLevel;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
@Slf4j
public class TokenCheckTest extends RunningServerTest {
	
	private void assertFailedJwt(TokenCheckResponse tcr) {
		assertFalse(tcr.isHadToken());
	}
	
	@Test
	public void checkNoAuthHeader() {
		ValidatableResponse response = given()
			.when()
			.get("/api/user/auth/tokenCheck")
			.then()
			.statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void checkEmptyAuth() {
		ValidatableResponse response = given()
			.when()
			.header(new Header("Authentication", null))
			.get("/api/user/auth/tokenCheck")
			.then()
			.statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void checkEmptyJwt() {
		ValidatableResponse response = given()
			.when()
			.header(new Header("Authorization", "Bearer"))
			.get("/api/user/auth/tokenCheck")
			.then()
			.statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
	}
	
	//TODO:: refactor these
	@Test
	public void checkValidUserJwt() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		testUser.setLevel(UserLevel.REGULAR);
		testUser.update();
		
		String token = this.userUtils.getTestUserJwt(testUser);
		
		ValidatableResponse response = given()
			.when()
			.header(new Header("Authorization", "Bearer " + token))
			.get("/api/user/auth/tokenCheck")
			.then()
			.statusCode(Response.Status.OK.getStatusCode());
		
		log.info("Response with jwt from tokenCheck: {}", response.extract().response().asString());
		TokenCheckResponse tokenCheckResponse = response.extract().body().as(TokenCheckResponse.class);
		
		assertTrue(tokenCheckResponse.isHadToken());
		assertFalse(tokenCheckResponse.isExpired());
		//        assertTrue(tokenCheckResponse.isTokenSecure()); //fails due to no https?
		//TODO:: check expiration
	}
	
	@Test
	public void checkValidAdminJwt() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		testUser.setLevel(UserLevel.ADMIN);
		testUser.update();
		
		String token = this.userUtils.getTestUserJwt(testUser);
		
		ValidatableResponse response = given()
			.when()
			.header(new Header("Authorization", "Bearer " + token))
			.get("/api/user/auth/tokenCheck")
			.then()
			.statusCode(Response.Status.OK.getStatusCode());
		
		log.info("Response with jwt from tokenCheck: {}", response.extract().response().asString());
		TokenCheckResponse tokenCheckResponse = response.extract().body().as(TokenCheckResponse.class);
		
		assertTrue(tokenCheckResponse.isHadToken());
		assertFalse(tokenCheckResponse.isExpired());
		//        assertTrue(tokenCheckResponse.isTokenSecure()); //fails due to no https?
		
		//TODO:: check expiration
	}
}