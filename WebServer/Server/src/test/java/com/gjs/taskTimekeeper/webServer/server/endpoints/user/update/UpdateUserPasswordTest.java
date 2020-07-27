package com.gjs.taskTimekeeper.webServer.server.endpoints.user.update;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import com.gjs.taskTimekeeper.webServer.server.testResources.rest.TestRestUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.update.UserUpdatePasswordRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class UpdateUserPasswordTest extends RunningServerTest {
	
	public void assertLogin(TestUser testUser, String password) {
		User user = testUser.getUserObj();
		
		this.userUtils.getPasswordService().assertPasswordMatchesHash(
			user.getHashedPass(),
			password
		);
	}
	
	@Test
	public void testUserUpdatePassword() {
		TestUser testUser = this.userUtils.setupTestUser(true);
		
		String newPassword = this.userUtils.getTokenService().generateToken();
		
		ValidatableResponse response = TestRestUtils.newJwtCall(this.userUtils.getTestUserJwt(testUser))
													.body(new UserUpdatePasswordRequest(
														testUser.getPlainPassword(),
														newPassword
													)).contentType(ContentType.JSON)
													.patch("/api/user/update/password").then();
		response.statusCode(NO_CONTENT.getStatusCode());
		
		this.assertLogin(testUser, newPassword);
		//TODO:: check for email notification
	}
	
	//TODO:: negative tests
}