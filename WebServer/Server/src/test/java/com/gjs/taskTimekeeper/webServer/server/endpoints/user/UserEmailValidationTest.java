package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.ServerWebUiTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserRegistrationRequest;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserRegistrationResponse;
import io.quarkus.mailer.Mail;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils.assertErrorMessage;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
@Slf4j
public class UserEmailValidationTest extends ServerWebUiTest {
	
	public static final String USER_REGISTRATION_ENDPOINT = "/api/user/registration";
	
	protected UserEmailValidationTest(ServerInfoBean infoBean, WebDriverWrapper wrapper) {
		super(infoBean, wrapper);
	}
	
	private UserRegistrationRequest getTestRequest() {
		TestUser testUser = this.userUtils.setupTestUser(false);
		
		return new UserRegistrationRequest(
			testUser.getUsername(),
			testUser.getEmail(),
			testUser.getPlainPassword()
		);
	}
	
	private UserRegistrationResponse doNewUserRequest() {
		UserRegistrationRequest registrationRequest = this.getTestRequest();
		Response response = given()
			.when()
			.contentType(ContentType.JSON)
			.body(registrationRequest)
			.post(USER_REGISTRATION_ENDPOINT);
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.CREATED.getStatusCode());
		
		UserRegistrationResponse registrationResponse = response.as(UserRegistrationResponse.class);
		
		log.debug("User registration response: {}", registrationResponse);
		
		return registrationResponse;
	}
	
	private URL getValidationLinkFromEmail(UserRegistrationResponse registrationResponse) throws MalformedURLException {
		List<Mail> sent = mailbox.getMessagesSentTo(registrationResponse.getEmail());
		assertEquals(1, sent.size());
		Mail actual = sent.get(0);
		
		assertEquals(
			"Welcome to the TaskTimekeeper Server",
			actual.getSubject()
		);
		
		log.debug("Email from registration: {}", actual.getHtml());
		
		Document emailDoc = Jsoup.parse(actual.getHtml());
		
		Element emailLink = emailDoc.getElementById("validationLink");
		URL validationUrl = new URL(emailLink.text());
		log.debug("Validation link: {}", validationUrl);
		return validationUrl;
	}
	
	private Map<String, String> getParams(String params) {
		Map<String, String> output = new HashMap<>();
		
		for(String param : params.split("&")) {
			String[] parts = param.split("=");
			output.put(parts[0], parts[1]);
		}
		
		return output;
	}
	
	private void assertNotValidated(String email) {
		User newUser = User.findByEmail(email);
		
		assertFalse(newUser.isEmailValidated());
		assertNotNull(newUser.getEmailValidationTokenHash());
		assertNull(newUser.getLastEmailValidated());
	}
	
	@Test
	public void validateNewUserTest() throws MalformedURLException {
		UserRegistrationResponse registrationResponse = this.doNewUserRequest();
		
		URL validationUrl = this.getValidationLinkFromEmail(registrationResponse);
		
		this.wrapper.navigateToServer(validationUrl);
		
		WebAssertions.assertPageHasMessage(
			this.wrapper,
			"success",
			null,
			UserEmailValidation.SUCCESSFULLY_VALIDATED_EMAIL_MESSAGE
		);
		
		User newUser = User.findByEmail(registrationResponse.getEmail());
		
		assertTrue(newUser.isEmailValidated());
		assertNull(newUser.getEmailValidationTokenHash());
		assertNotNull(newUser.getLastEmailValidated());
	}
	
	@Test
	public void validateNewUserBadUserIdTest() throws MalformedURLException {
		UserRegistrationResponse registrationResponse = this.doNewUserRequest();
		
		URL validationUrl = this.getValidationLinkFromEmail(registrationResponse);
		
		Map<String, String> query = this.getParams(validationUrl.getQuery());
		
		query.put("userId", new ObjectId().toHexString());
		
		Response response = given()
			.when()
			.basePath(validationUrl.getPath())
			.queryParams(query)
			.get();
		
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode());
		
		
		User newUser = User.findByEmail(registrationResponse.getEmail());
		
		assertFalse(newUser.isEmailValidated());
		assertNotNull(newUser.getEmailValidationTokenHash());
		assertNull(newUser.getLastEmailValidated());
		
		assertErrorMessage("User was not found.", response.asString());
	}
	
	@Test
	public void validateNewUserNoUserIdTest() throws MalformedURLException {
		UserRegistrationResponse registrationResponse = this.doNewUserRequest();
		
		URL validationUrl = this.getValidationLinkFromEmail(registrationResponse);
		
		Map<String, String> query = this.getParams(validationUrl.getQuery());
		
		query.remove("userId");
		
		Response response = given()
			.when()
			.basePath(validationUrl.getPath())
			.queryParams(query)
			.get();
		
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
		
		assertNotValidated(registrationResponse.getEmail());
		
		assertErrorMessage("No user id given.", response.asString());
	}
	
	@Test
	public void validateNewUserBadValidationTokenTest() throws MalformedURLException {
		UserRegistrationResponse registrationResponse = this.doNewUserRequest();
		
		URL validationUrl = this.getValidationLinkFromEmail(registrationResponse);
		
		Map<String, String> query = this.getParams(validationUrl.getQuery());
		
		query.put("validationToken", "helloworld");
		
		Response response = given()
			.when()
			.basePath(validationUrl.getPath())
			.queryParams(query)
			.get();
		
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
		
		
		this.assertNotValidated(registrationResponse.getEmail());
		
		assertErrorMessage("Token given was invalid.", response.asString());
	}
	
	@Test
	public void validateNewUserNoValidationTokenTest() throws MalformedURLException {
		UserRegistrationResponse registrationResponse = this.doNewUserRequest();
		
		URL validationUrl = this.getValidationLinkFromEmail(registrationResponse);
		
		Map<String, String> query = this.getParams(validationUrl.getQuery());
		
		query.remove("validationToken");
		
		Response response = given()
			.when()
			.basePath(validationUrl.getPath())
			.queryParams(query)
			.get();
		
		response.then()
				.statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
		
		
		this.assertNotValidated(registrationResponse.getEmail());
		
		assertErrorMessage("No token given.", response.asString());
	}
}