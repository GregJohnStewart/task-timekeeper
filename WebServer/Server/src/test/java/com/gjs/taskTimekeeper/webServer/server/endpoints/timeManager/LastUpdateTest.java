package com.gjs.taskTimekeeper.webServer.server.endpoints.timeManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.TimeManagerLastUpdateCheckResponse;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.TimeManagerResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class LastUpdateTest extends RunningServerTest {
	private static final ObjectMapper MANAGER_MAPPER = ObjectMapperUtilities.getTimeManagerObjectMapper();
	
	private User testUser;
	private String testUserJwt;
	private ManagerEntity testEntity;
	
	@PostConstruct
	public void setupTestUser() {
		this.testUser = userUtils.setupTestUser(true).getUserObj();
		this.testUserJwt = this.userUtils.getTestUserJwt(testUser);
	}
	
	private void assertEntityEqualsResponse(ManagerEntity managerEntity, TimeManagerResponse response)
		throws JsonProcessingException {
		assertEquals(
			managerEntity.getLastUpdate(),
			response.getLastUpdated()
		);
		assertArrayEquals(
			managerEntity.getTimeManagerData(),
			MANAGER_MAPPER.writeValueAsBytes(response.getTimeManagerData())
		);
	}
	
	private void setupExisting() throws JsonProcessingException {
		this.testEntity = new ManagerEntity(
			this.testUser.id,
			MANAGER_MAPPER.writeValueAsBytes(new TimeManager().addTask(new Task("New Task"))),
			new Date()
		);
		this.testEntity.persist();
	}
	
	@Test
	public void testGetLastUpdateNoEntity() throws IOException {
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.testUserJwt)
															   .get("/api/timeManager/lastUpdate").then();
		
		validatableResponse.statusCode(Response.Status.NO_CONTENT.getStatusCode());
	}
	
	@Test
	public void testGetLastUpdate() throws IOException {
		this.setupExisting();
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.testUserJwt)
															   .get("/api/timeManager/lastUpdate").then();
		
		validatableResponse.statusCode(Response.Status.OK.getStatusCode());
		
		TimeManagerLastUpdateCheckResponse response = validatableResponse.extract().body().as(
			TimeManagerLastUpdateCheckResponse.class);
		
		assertEquals(
			ManagerEntity.findByUserId(this.testUser.id).getLastUpdate(),
			response.getLastUpdate()
		);
	}
	
	//TODO:: test bad/ no jwt
}