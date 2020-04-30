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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class UserEmailValidationTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEmailValidationTest.class);

    private static final String USER_EMAIL = "test@testing.tst";
    public static final String USER_REGISTRATION_ENDPOINT = "/api/user/registration";

    @Inject
    MockMailbox mailbox;


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
        assertTrue(sent.isEmpty());
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

    private UserRegistrationResponse doNewUserRequest(){
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

        return registrationResponse;
    }

    private URL getValudationLinkFromEmail(UserRegistrationResponse registrationResponse) throws MalformedURLException {
        List<Mail> sent = mailbox.getMessagesSentTo(registrationResponse.getEmail());
        assertEquals(1, sent.size());
        Mail actual = sent.get(0);

        assertEquals(
                "Welcome to the TaskTimekeeper Server",
                actual.getSubject()
        );

        LOGGER.debug("Email from registration: {}", actual.getHtml());

        Document emailDoc = Jsoup.parse(actual.getHtml());

        Element emailLink = emailDoc.getElementById("validationLink");
        URL validationUrl = new URL(emailLink.text());
        LOGGER.debug("Validation link: {}", validationUrl);
        return validationUrl;
    }

    private Map<String, String> getParams(String params){
        Map<String, String> output = new HashMap<>();

        for(String param : params.split("&")){
            String[] parts = param.split("=");
            output.put(parts[0], parts[1]);
        }

        return output;
    }



    @Test
    public void validateNewUserTest() throws MalformedURLException {
        UserRegistrationResponse registrationResponse = this.doNewUserRequest();

        URL validationUrl = this.getValudationLinkFromEmail(registrationResponse);


        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .basePath(validationUrl.getPath())
                .queryParams(this.getParams(validationUrl.getQuery()))
                .get();

        response.then()
                .statusCode(javax.ws.rs.core.Response.Status.OK.getStatusCode());

        //TODO:: assert user was validated
    }

    //TODO:: test bad userId
    //TODO:: test bad token
}