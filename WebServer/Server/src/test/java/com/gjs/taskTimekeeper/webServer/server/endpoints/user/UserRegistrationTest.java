package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class UserRegistrationTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistrationTest.class);

    private static final String USER_EMAIL = "test@testing.tst";

    @Inject
    MockMailbox mailbox;

    @BeforeEach
    void init() {
        mailbox.clear();
    }

    private UserRegistrationRequest getTestRequest(){
        return new UserRegistrationRequest(
                "test_user",
                USER_EMAIL,
                PasswordServiceTest.GOOD_PASS
        );
    }

    @Test
    public void registerUserTest(){
        UserRegistrationRequest registrationRequest = this.getTestRequest();
         Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post("/api/user/registration");
        response.then()
            .statusCode(javax.ws.rs.core.Response.Status.CREATED.getStatusCode());

        UserRegistrationResponse registrationResponse = response.as(UserRegistrationResponse.class);

        LOGGER.debug("User registration response: {}", registrationResponse);


        List<Mail> sent = mailbox.getMessagesSentTo(registrationResponse.getEmail());
        assertEquals(1, sent.size());
        Mail actual = sent.get(0);

        assertEquals(
                "Welcome to the TaskTimekeeper Server",
                actual.getSubject()
        );

        LOGGER.debug("Email from registration: {}", actual.getHtml());
    }

    //TODO:: first user test
    //TODO:: second user test
    //TODO:: bad username test
    //TODO:: bad password test
    //TODO:: bad email test

}