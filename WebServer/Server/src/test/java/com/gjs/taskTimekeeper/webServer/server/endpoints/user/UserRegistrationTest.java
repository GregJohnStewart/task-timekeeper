package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.service.PasswordServiceTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserRegistrationRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class UserRegistrationTest extends RunningServerTest {

    private UserRegistrationRequest getTestRequest(){
        return new UserRegistrationRequest(
                "test_user",
                "test@testing.tst",
                PasswordServiceTest.GOOD_PASS
        );
    }

    @Test
    public void registerUserTest(){
        UserRegistrationRequest registrationRequest = this.getTestRequest();
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .post("/api/user/registration")
            .then()
            .statusCode(Response.Status.CREATED.getStatusCode());
    }

}