package com.gjs.taskTimekeeper.webServer.server.health;

import com.fasterxml.jackson.databind.JsonNode;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.gjs.taskTimekeeper.webServer.server.health.Liveliness.SIMPLE_LIVELINESS_HEALTH_CHECK;
import static com.gjs.taskTimekeeper.webServer.server.testResources.rest.HealthUtils.assertHealthCheckContains;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class LivelinessTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LivelinessTest.class);

    @Test
    public void testHealthLiveEndpoint() throws IOException {
        ValidatableResponse response = given()
                .when().get("/health")
                .then()
                .statusCode(200);

        JsonNode responseNode = response.extract().as(JsonNode.class);

        assertHealthCheckContains(SIMPLE_LIVELINESS_HEALTH_CHECK, responseNode);
    }
}
