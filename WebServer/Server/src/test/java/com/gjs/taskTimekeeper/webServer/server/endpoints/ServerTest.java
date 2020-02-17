package com.gjs.taskTimekeeper.webServer.server.endpoints;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.ServerInfo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
class ServerTest extends RunningServerTest {

    @Inject
    ServerInfoBean infoBean;

    @Test
    void getServerInfo() {
        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .get("/api/server/info");
        response
                .then()
                .statusCode(javax.ws.rs.core.Response.Status.OK.getStatusCode());
        ServerInfo info = response.as(ServerInfo.class);

        assertEquals(infoBean.getOrganization().get(), info.getOrganization());
        assertEquals(infoBean.getServerName().get(), info.getServerName());
        assertEquals(infoBean.getUrl().get().toString(), info.getUrl());
        assertEquals(infoBean.getContactInfo().getName().get(), info.getContactInfo().getName());
        assertEquals(infoBean.getContactInfo().getEmail().get(), info.getContactInfo().getEmail());
        assertEquals(infoBean.getContactInfo().getPhone().get(), info.getContactInfo().getPhone());
    }
}