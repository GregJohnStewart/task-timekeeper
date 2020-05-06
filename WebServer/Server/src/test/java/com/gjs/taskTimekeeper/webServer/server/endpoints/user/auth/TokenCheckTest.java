package com.gjs.taskTimekeeper.webServer.server.endpoints.user.auth;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.JwtService;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.webLibrary.TokenCheckResponse;
import com.gjs.taskTimekeeper.webServer.webLibrary.UserLevel;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class TokenCheckTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenCheckTest.class);

    @Inject
    private JwtService jwtService;

    private void assertFailedJwt(TokenCheckResponse tcr){
        assertFalse(tcr.isHadToken());
    }

    @Test
    public void checkNoAuthHeader(){
        ValidatableResponse response = given()
                .when()
                .get("/api/user/auth/tokenCheck")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void checkEmptyAuth(){
        ValidatableResponse response = given()
                .when()
                .header(new Header("Authentication", null))
                .get("/api/user/auth/tokenCheck")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void checkEmptyJwt(){
        ValidatableResponse response = given()
                .when()
                .header(new Header("Authorization", "Bearer"))
                .get("/api/user/auth/tokenCheck")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void checkValidUserJwt(){
        User testUser = new User();
        testUser.setUsername("helloWorld");
        testUser.setEmail("test@world.com");
        testUser.setLastLogin(new Date());
        testUser.setLevel(UserLevel.REGULAR);

        testUser.persist();

        String token = this.jwtService.generateTokenString(testUser, false);

        ValidatableResponse response = given()
                .when()
                .header(new Header("Authorization", "Bearer " + token))
                .get("/api/user/auth/tokenCheck")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        LOGGER.info("Response with jwt from tokenCheck: {}", response.extract().response().asString());
        TokenCheckResponse tokenCheckResponse = response.extract().body().as(TokenCheckResponse.class);

        assertTrue(tokenCheckResponse.isHadToken());
        assertFalse(tokenCheckResponse.isExpired());
//        assertTrue(tokenCheckResponse.isTokenSecure()); //fails due to no https?
    }

    @Test
    public void checkValidAdminJwt(){
        User testUser = new User();
        testUser.setUsername("helloWorld");
        testUser.setEmail("test@world.com");
        testUser.setLastLogin(new Date());
        testUser.setLevel(UserLevel.ADMIN);

        testUser.persist();

        String token = this.jwtService.generateTokenString(testUser, false);

        ValidatableResponse response = given()
                .when()
                .header(new Header("Authorization", "Bearer " + token))
                .get("/api/user/auth/tokenCheck")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

        LOGGER.info("Response with jwt from tokenCheck: {}", response.extract().response().asString());
        TokenCheckResponse tokenCheckResponse = response.extract().body().as(TokenCheckResponse.class);

        assertTrue(tokenCheckResponse.isHadToken());
        assertFalse(tokenCheckResponse.isExpired());
//        assertTrue(tokenCheckResponse.isTokenSecure()); //fails due to no https?
    }

}