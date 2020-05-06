package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.UserUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.UserLevel;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Date;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class JwtServiceTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceTest.class);

    @Inject
    private JwtService jwtService;

    @Inject
    private PasswordService passwordService;

    @Inject
    private UserUtils userUtils;

    private User testUser;

    @BeforeEach
    public void setTestUser(){
        this.testUser = this.userUtils.getTestUser(true);
        this.testUser.setLastLogin(new Date());
        this.testUser.setNumLogins(1L);
    }

    private void assertProperToken(String token){
        ValidatableResponse response = given()
                .when()
                .header(new Header("Authorization", "Bearer " + token))
                .get("/api/user/auth/tokenCheck")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }


    @Test
    public void test(){
        this.testUser.setLevel(UserLevel.REGULAR);

        String jwt = jwtService.generateTokenString(this.testUser, false);

        LOGGER.info("Created test user's jwt: {}", jwt);

        this.assertProperToken(jwt);
    }

    //TODO:: check for expiration
    //TODO:: check for user level
}
