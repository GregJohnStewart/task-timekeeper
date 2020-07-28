package com.gjs.taskTimekeeper.webServer.server.endpoints.timeManager;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.action.TimeManagerActionRequest;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.action.TimeManagerActionResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity.MANAGER_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class ActionDoerTest extends RunningServerTest {
	
	private TestUser testUser;
	private String testUserJwt;
	private ManagerEntity testEntity;
	
	@PostConstruct
	public void setupTestUser() {
		this.testUser = userUtils.setupTestUser(true);
		this.testUserJwt = this.userUtils.getTestUserJwt(testUser);
	}
	
	
	@Test
	public void testUpdateUserManagerEmptyConfig() throws IOException {
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(testUserJwt)
															   .contentType(ContentType.JSON)
															   .body(new TimeManagerActionRequest())
															   .patch("/api/timeManager/manager/action")
															   .then();
		
		validatableResponse.statusCode(Response.Status.OK.getStatusCode());
		
		ManagerEntity entity = ManagerEntity.findByUserId(testUser.getUserObj().id);
		
		assertEquals(
			new TimeManager(),
			MANAGER_MAPPER.readValue(entity.getTimeManagerData(), TimeManager.class)
		);
		
		TimeManagerActionResponse response = validatableResponse.extract().as(TimeManagerActionResponse.class);
		
		assertEquals(
			entity.getLastUpdate(),
			response.getLastUpdated()
		);
		assertEquals(
			new TimeManager(),
			response.getTimeManagerData()
		);
		assertEquals(
			"",
			response.getRegOut()
		);
		assertEquals(
			"",
			response.getErrOut()
		);
	}
	
	//TODO:: error test, actual update test
}