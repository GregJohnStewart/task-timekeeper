package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserInfo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils.assertErrorMessage;
import static com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserLevel.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
@Slf4j
public class UserInfoTest extends RunningServerTest {
	
	@Test
	public void testUserOwnInfo() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.userUtils.getTestUserJwt(testUser)).get(
			"/api/user/info/self").then();
		
		validatableResponse.statusCode(Response.Status.OK.getStatusCode());
		
		assertEquals(
			testUser.toUserInfo(),
			validatableResponse.extract().as(UserInfo.class)
		);
	}
	
	@Test
	public void testAdminUserOwnInfo() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		testUser.setLevel(ADMIN);
		testUser.update();
		
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.userUtils.getTestUserJwt(testUser)).get(
			"/api/user/info/self").then();
		
		validatableResponse.statusCode(Response.Status.OK.getStatusCode());
		
		assertEquals(
			testUser.toUserInfo(),
			validatableResponse.extract().as(UserInfo.class)
		);
	}
	
	@Test
	public void testUserInfoByIdRegularUserOwn() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		
		
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.userUtils.getTestUserJwt(testUser)).get(
			"/api/user/info/" + testUser.id.toHexString()).then();
		
		validatableResponse.statusCode(Response.Status.FORBIDDEN.getStatusCode());
		
		assertErrorMessage(
			"",
			validatableResponse
		);
	}
	
	@Test
	public void testUserInfoByIdRegularUserOther() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		User testUserTwo = this.userUtils.setupTestUser(true).getUserObj();
		
		
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.userUtils.getTestUserJwt(testUser)).get(
			"/api/user/info/" + testUserTwo.id.toHexString()).then();
		
		validatableResponse.statusCode(Response.Status.FORBIDDEN.getStatusCode());
		
		assertErrorMessage(
			"",
			validatableResponse
		);
	}
	
	@Test
	public void testUserInfoByIdAdminOwn() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		testUser.setLevel(ADMIN);
		testUser.update();
		
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.userUtils.getTestUserJwt(testUser)).get(
			"/api/user/info/" + testUser.id.toHexString()).then();
		
		validatableResponse.statusCode(Response.Status.OK.getStatusCode());
		
		assertEquals(
			testUser.toUserInfo(),
			validatableResponse.extract().as(UserInfo.class)
		);
	}
	
	@Test
	public void testUserInfoByIdAdminOther() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		testUser.setLevel(ADMIN);
		testUser.update();
		User testUserTwo = this.userUtils.setupTestUser(true).getUserObj();
		
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.userUtils.getTestUserJwt(testUser)).get(
			"/api/user/info/" + testUserTwo.id.toHexString()).then();
		
		validatableResponse.statusCode(Response.Status.OK.getStatusCode());
		
		assertEquals(
			testUserTwo.toUserInfo(),
			validatableResponse.extract().as(UserInfo.class)
		);
	}
	
	@Test
	public void testUsersInfoByIdRegularUser() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		User testUserTwo = this.userUtils.setupTestUser(true).getUserObj();
		
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.userUtils.getTestUserJwt(testUser)).get(
			"/api/user/info/").then();
		
		validatableResponse.statusCode(Response.Status.FORBIDDEN.getStatusCode());
		
		assertErrorMessage(
			"",
			validatableResponse
		);
	}
	
	private void assertUserInResults(User user, List<UserInfo> userInfo) {
		UserInfo expected = user.toUserInfo();
		
		for(UserInfo curInfo : userInfo) {
			if(curInfo.equals(expected)) {
				return;
			}
		}
		fail("Expected user not found in result set.");
	}
	
	@Test
	public void testUsersInfoByIdAdminUser() {
		User testUser = this.userUtils.setupTestUser(true).getUserObj();
		testUser.setLevel(ADMIN);
		testUser.update();
		User testUserTwo = this.userUtils.setupTestUser(true).getUserObj();
		
		ValidatableResponse validatableResponse = TestRestUtils.newJwtCall(this.userUtils.getTestUserJwt(testUser)).get(
			"/api/user/info/").then();
		
		validatableResponse.statusCode(Response.Status.OK.getStatusCode());
		
		List<UserInfo> returned = validatableResponse.extract().as(new TypeRef<List<UserInfo>>() {
		});
		log.debug("Got {} results from get all user info.", returned.size());
		
		assertUserInResults(testUser, returned);
		assertUserInResults(testUserTwo, returned);
	}
}