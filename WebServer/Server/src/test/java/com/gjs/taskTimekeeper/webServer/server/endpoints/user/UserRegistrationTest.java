package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserLevel;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserRegistrationRequest;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserRegistrationResponse;
import io.quarkus.mailer.Mail;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

import static com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils.assertErrorMessage;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
@Execution(ExecutionMode.SAME_THREAD)
@Slf4j
public class UserRegistrationTest extends RunningServerTest {
	
	public static final String USER_REGISTRATION_ENDPOINT = "/api/user/registration";
	
	@Inject
	PasswordService passwordService;
	
	@BeforeEach
	void init() {
		mailbox.clear();
	}
	
	private void assertEmailNotSent(String email) {
		List<Mail> sent = mailbox.getMessagesSentTo(email);
		assertNull(sent);
	}
	
	private void assertEmailSent(String email) {
		List<Mail> sent = mailbox.getMessagesSentTo(email);
		assertEquals(1, sent.size());
		Mail actual = sent.get(0);
		
		assertEquals(
			"Welcome to the TaskTimekeeper Server",
			actual.getSubject()
		);
		
		log.debug("Email from registration: {}", actual.getHtml());
	}
	
	private void assertUserEqualsRegistration(
		UserRegistrationRequest request,
		UserRegistrationResponse response,
		User newUser
	) {
		assertEquals(response.getUsername(), newUser.getUsername());
		assertEquals(request.getEmail(), newUser.getEmail());
		
		assertFalse(newUser.isEmailValidated());
		assertNull(newUser.getLastEmailValidated());
		assertNotNull(newUser.getJoinDateTime());
		assertNotNull(newUser.getNoLoginsBefore());
		assertEquals(newUser.getJoinDateTime(), newUser.getNoLoginsBefore());
		
		passwordService.assertPasswordMatchesHash(newUser.getHashedPass(), request.getPlainPassword());
	}
	
	@Test
	public void registerFirstUserTest() {
		this.cleanupDatabaseAndMail();
		
		UserRegistrationRequest registrationRequest = TestRestUtils.getUserRegistrationRequest(this.userUtils.setupTestUser());
		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(registrationRequest)
			.post(USER_REGISTRATION_ENDPOINT);
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.CREATED.getStatusCode());
		
		UserRegistrationResponse registrationResponse = response.as(UserRegistrationResponse.class);
		
		log.debug("User registration response: {}", registrationResponse);
		
		this.assertEmailSent(registrationResponse.getEmail());
		
		User newUser = User.findByEmail(registrationResponse.getEmail());
		
		assertUserEqualsRegistration(
			registrationRequest,
			registrationResponse,
			newUser
		);
		
		assertTrue(newUser.isApprovedUser());
		assertEquals(UserLevel.ADMIN, newUser.getLevel());
	}
	
	@Test
	public void registerSecondUserTest() {
		User firstUser = new User();
		firstUser.persist();
		
		UserRegistrationRequest registrationRequest = TestRestUtils.getUserRegistrationRequest(this.userUtils.setupTestUser());
		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(registrationRequest)
			.post(USER_REGISTRATION_ENDPOINT);
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.CREATED.getStatusCode());
		
		UserRegistrationResponse registrationResponse = response.as(UserRegistrationResponse.class);
		
		log.debug("User registration response: {}", registrationResponse);
		
		this.assertEmailSent(registrationResponse.getEmail());
		
		User newUser = User.findByEmail(registrationResponse.getEmail());
		
		assertUserEqualsRegistration(
			registrationRequest,
			registrationResponse,
			newUser
		);
		
		assertFalse(newUser.isApprovedUser());
		assertEquals(UserLevel.REGULAR, newUser.getLevel());
	}
	
	@Test
	public void registerUserDuplicateUsernameTest() {
		String username = "helloWorld";
		User firstUser = new User();
		firstUser.setUsername(username);
		firstUser.persist();
		
		UserRegistrationRequest registrationRequest = TestRestUtils.getUserRegistrationRequest(this.userUtils.setupTestUser());
		registrationRequest.setUsername(username);
		
		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(registrationRequest)
			.post(USER_REGISTRATION_ENDPOINT);
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
		
		assertErrorMessage("Username already exists.", response.asString());
		assertEmailNotSent(registrationRequest.getEmail());
	}
	
	@ParameterizedTest
	@MethodSource("badUserNames")
	public void registerUserBadUsernameTest(String badUsername, String expectedError) {
		UserRegistrationRequest registrationRequest = TestRestUtils.getUserRegistrationRequest(this.userUtils.setupTestUser());
		
		registrationRequest.setUsername(badUsername);
		
		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(registrationRequest)
			.post(USER_REGISTRATION_ENDPOINT);
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
		assertErrorMessage(expectedError, response.asString());
		assertEmailNotSent(registrationRequest.getEmail());
	}
	
	public static Stream<Arguments> badUserNames() {
		return Stream.of(
			Arguments.of(
				"",
				"Username too short. Must be at least 1 character\\(s\\) long."
			),
			Arguments.of(
				(String)null,
				"Username cannot be null."
			),
			Arguments.of(
				" ",
				"Username too short. Must be at least 1 character\\(s\\) long."
			)
		);
	}
	
	@Test
	public void registerUserBadPasswordTest() {
		UserRegistrationRequest registrationRequest = TestRestUtils.getUserRegistrationRequest(this.userUtils.setupTestUser());
		
		registrationRequest.setPlainPassword("");
		
		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(registrationRequest)
			.post(USER_REGISTRATION_ENDPOINT);
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
		assertErrorMessage(
			"Password is too short. Must be at least 8 characters. Was 0 character\\(s\\).",
			response.asString()
		);
		assertEmailNotSent(registrationRequest.getEmail());
	}
	
	@Test
	public void registerUserBadEmailTest() {
		UserRegistrationRequest registrationRequest = TestRestUtils.getUserRegistrationRequest(this.userUtils.setupTestUser());
		
		registrationRequest.setEmail("");
		
		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(registrationRequest)
			.post(USER_REGISTRATION_ENDPOINT);
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
		assertErrorMessage("Received an invalid email.", response.asString());
		assertEmailNotSent(registrationRequest.getEmail());
	}
	
	@Test
	public void registerUserDuplicateEmailTest() {
		TestUser firstTestUser = this.userUtils.setupTestUser(true);
		User firstUser = firstTestUser.getUserObj();
		
		TestUser secondUser = this.userUtils.setupTestUser().setEmail(firstTestUser.getEmail());
		UserRegistrationRequest registrationRequest = TestRestUtils.getUserRegistrationRequest(secondUser);
		registrationRequest.setEmail(firstTestUser.getEmail());
		
		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(registrationRequest)
			.post(USER_REGISTRATION_ENDPOINT);
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
		assertErrorMessage("Email already exists.", response.asString());
		assertEmailNotSent(registrationRequest.getEmail());
	}
}