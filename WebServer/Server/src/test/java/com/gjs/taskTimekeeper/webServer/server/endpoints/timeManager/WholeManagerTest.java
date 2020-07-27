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
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.TimeManagerResponse;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.whole.WholeTimeManagerUpdateRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class WholeManagerTest extends RunningServerTest {
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
	public void testGetNewWholeManager() throws IOException {
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.testUserJwt)
															   .get("/api/timeManager/manager").then();
		
		validatableResponse.statusCode(Response.Status.OK.getStatusCode());
		
		TimeManagerResponse response = validatableResponse.extract().body().as(TimeManagerResponse.class);
		assertNull(response.getLastUpdated());
		
		
		this.testEntity = ManagerEntity.findByUserId(testUser.id);
		assertNull(this.testEntity.getLastUpdate());
		
		this.assertEntityEqualsResponse(
			this.testEntity,
			response
		);
		
		assertEquals(
			response.getTimeManagerData(),
			new TimeManager()
		);
	}
	
	@Test
	public void testGetExistingWholeManager() throws IOException {
		this.setupExisting();
		
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.testUserJwt)
															   .get("/api/timeManager/manager").then();
		
		validatableResponse.statusCode(Response.Status.OK.getStatusCode());
		
		TimeManagerResponse response = validatableResponse.extract().body().as(TimeManagerResponse.class);
		assertNotNull(response.getLastUpdated());
		
		ManagerEntity updatedEntity = ManagerEntity.findByUserId(testUser.id);
		assertNotNull(updatedEntity.getLastUpdate());
		
		assertEntityEqualsResponse(
			updatedEntity,
			response
		);
		
		assertEquals(
			response.getTimeManagerData(),
			MANAGER_MAPPER.readValue(this.testEntity.getTimeManagerData(), TimeManager.class)
		);
	}
	
	@Test
	public void testGetWholeManagerBadAuth() throws JsonProcessingException {
		this.setupExisting();
		
		ManagerEntity previousEntity = this.testEntity;
		
		ValidatableResponse validatableResponse = given().get("/api/timeManager/manager").then();
		
		validatableResponse.assertThat().statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
		
		assertEquals(
			previousEntity,
			ManagerEntity.findByUserId(this.testUser.id)
		);
	}
	
	//TODO:: post
	//TODO:: post bad data
	
	@Test
	public void testPatchWholeManagerBadAuth() throws JsonProcessingException {
		this.setupExisting();
		
		ManagerEntity previousEntity = this.testEntity;
		
		ValidatableResponse validatableResponse = given()
			.contentType(ContentType.JSON)
			.body(new WholeTimeManagerUpdateRequest(new TimeManager()))
			.patch("/api/timeManager/manager")
			.then();
		
		validatableResponse.assertThat().statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
		
		assertEquals(
			previousEntity,
			ManagerEntity.findByUserId(this.testUser.id)
		);
	}
}