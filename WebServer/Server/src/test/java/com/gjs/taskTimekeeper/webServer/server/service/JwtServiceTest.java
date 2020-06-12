package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class JwtServiceTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceTest.class);

    @Inject
    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    public void setTestUser(){
        this.testUser = this.userUtils.setupTestUser(true).getUserObj();
		this.testUser.setNumLogins(1L);
    }

    private void assertProperToken(String token){
        RequestSpecification call = given()
                .when();
        TestRestUtils.setupJwtCall(call, token);

        ValidatableResponse response = call
                .get("/api/user/auth/tokenCheck")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    public void test(){
        String jwt = jwtService.generateTokenString(this.testUser, false);

        LOGGER.info("Created test user's jwt: {}", jwt);

        this.assertProperToken(jwt);
    }

    //TODO:: check for expiration
    //TODO:: check for user level
}
