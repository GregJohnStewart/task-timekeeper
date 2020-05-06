package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.UserLevel;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordServiceTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserRegistrationRequest;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserRegistrationResponse;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class UserRegistrationTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistrationTest.class);

    private static final String USER_EMAIL = "test@testing.tst";
    public static final String USER_REGISTRATION_ENDPOINT = "/api/user/registration";

    @Inject
    MockMailbox mailbox;

    @Inject
    PasswordService passwordService;

    @BeforeEach
    void init() {
        mailbox.clear();
    }

    private UserRegistrationRequest getTestRequest() {
        return new UserRegistrationRequest(
                "test_user",
                USER_EMAIL,
                PasswordServiceTest.GOOD_PASS
        );
    }

    private void assertEmailNotSent(String email) {
        List<Mail> sent = mailbox.getMessagesSentTo(email);
        assertNull(sent);
//        assertTrue(sent.isEmpty());
    }

    private void assertEmailSent(String email) {
        List<Mail> sent = mailbox.getMessagesSentTo(email);
        assertEquals(1, sent.size());
        Mail actual = sent.get(0);

        assertEquals(
                "Welcome to the TaskTimekeeper Server",
                actual.getSubject()
        );

        LOGGER.debug("Email from registration: {}", actual.getHtml());
    }

    private void assertUserEqualsRegistration(UserRegistrationRequest request, UserRegistrationResponse response, User newUser) {
        assertEquals(response.getUsername(), newUser.getUsername());
        assertEquals(request.getEmail(), newUser.getEmail());

        assertFalse(newUser.isEmailValidated());
        assertNull(newUser.getLastEmailValidated());
        assertNotNull(newUser.getJoinDateTime());
        assertNotNull(newUser.getNoLoginsBefore());
        assertEquals(newUser.getJoinDateTime(), newUser.getNoLoginsBefore());

        passwordService.assertPasswordMatchesHash(newUser.getHashedPass(), request.getPlainPassword());
    }

    private void assertErrorMessage(String expected, String message){
        if(!message.matches(expected)){
            fail("Error message \""+message+"\" did not match expected: \""+expected+"\"");
        }
    }

    @Test
    public void registerFirstUserTest() {
        UserRegistrationRequest registrationRequest = this.getTestRequest();
        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post(USER_REGISTRATION_ENDPOINT);
        response.then()
                .statusCode(javax.ws.rs.core.Response.Status.CREATED.getStatusCode());

        UserRegistrationResponse registrationResponse = response.as(UserRegistrationResponse.class);

        LOGGER.debug("User registration response: {}", registrationResponse);

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

        UserRegistrationRequest registrationRequest = this.getTestRequest();
        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post(USER_REGISTRATION_ENDPOINT);
        response.then()
                .statusCode(javax.ws.rs.core.Response.Status.CREATED.getStatusCode());

        UserRegistrationResponse registrationResponse = response.as(UserRegistrationResponse.class);

        LOGGER.debug("User registration response: {}", registrationResponse);

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

        UserRegistrationRequest registrationRequest = this.getTestRequest();
        registrationRequest.setUsername(username);

        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post(USER_REGISTRATION_ENDPOINT);
        response.then()
                .statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());

        this.assertErrorMessage("Username already exists.", response.asString());
        assertEmailNotSent(registrationRequest.getEmail());
    }

    @ParameterizedTest
    @MethodSource("badUserNames")
    public void registerUserBadUsernameTest(String badUsername, String expectedError) {
        UserRegistrationRequest registrationRequest = this.getTestRequest();

        registrationRequest.setUsername(badUsername);

        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post(USER_REGISTRATION_ENDPOINT);
        response.then()
                .statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
        this.assertErrorMessage(expectedError, response.asString());
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
        UserRegistrationRequest registrationRequest = this.getTestRequest();

        registrationRequest.setPlainPassword("");

        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post(USER_REGISTRATION_ENDPOINT);
        response.then()
                .statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
        this.assertErrorMessage("Password is too short. Must be at least 32 characters. Was 0 character\\(s\\).", response.asString());
        assertEmailNotSent(registrationRequest.getEmail());
    }

    @Test
    public void registerUserBadEmailTest() {
        UserRegistrationRequest registrationRequest = this.getTestRequest();

        registrationRequest.setEmail("");

        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post(USER_REGISTRATION_ENDPOINT);
        response.then()
                .statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
        this.assertErrorMessage("Received an invalid email.", response.asString());
        assertEmailNotSent(registrationRequest.getEmail());
    }

    @Test
    public void registerUserDuplicateEmailTest() {
        String email = "test@test.com";
        User firstUser = new User();
        firstUser.setEmail(email);
        firstUser.persist();

        UserRegistrationRequest registrationRequest = this.getTestRequest();
        registrationRequest.setEmail(email);

        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post(USER_REGISTRATION_ENDPOINT);
        response.then()
                .statusCode(javax.ws.rs.core.Response.Status.BAD_REQUEST.getStatusCode());
        this.assertErrorMessage("Email already exists.", response.asString());
        assertEmailNotSent(registrationRequest.getEmail());
    }
}