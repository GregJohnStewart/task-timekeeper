package com.gjs.taskTimekeeper.webServer.server.testResources.rest;

import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.fail;

public class TestRestUtils {

    public static void setupJwtCall(RequestSpecification call, String token){
        call.header(new Header("Authorization", "Bearer " + token));
    }

    public static RequestSpecification newJwtCall(String token){
        RequestSpecification request = given()
                .when();

        setupJwtCall(request, token);

        return request;
    }

    public static void assertErrorMessage(String expected, String message){
        if(!message.matches(expected)){
            fail("Error message \""+message+"\" did not match expected: \""+expected+"\"");
        }
    }
}
