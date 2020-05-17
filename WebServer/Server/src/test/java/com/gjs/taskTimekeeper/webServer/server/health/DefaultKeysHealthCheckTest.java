package com.gjs.taskTimekeeper.webServer.server.health;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class DefaultKeysHealthCheckTest {
    @Test
    public void testHealthLiveEndpoint() {
        given()
                .when().get("/health/live")
                .then()
                .statusCode(200);
        //TODO:: validate results
    }
}
