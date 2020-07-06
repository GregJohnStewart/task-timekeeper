package com.gjs.taskTimekeeper.webServer.server.templates;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.service.ServerUrlService;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.TestUser;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class TextEmailTemplateTest extends RunningServerTest {
	
	private final Template textEmailTemplate;
	private final ServerUrlService serverUrlService;
	private final ServerInfoBean serverInfoBean;
	
	public TextEmailTemplateTest(
		@ResourcePath("email/textEmailTemplate")
			Template textEmailTemplate,
		ServerUrlService serverUrlService,
		ServerInfoBean serverInfoBean
	) {
		this.textEmailTemplate = textEmailTemplate;
		this.serverUrlService = serverUrlService;
		this.serverInfoBean = serverInfoBean;
	}
	
	// TODO:: follow up with email happenings
	//		@Test
	public void testTextEmailTemplate() throws MalformedURLException {
		TestUser testUser = this.userUtils.setupTestUser(false);
		String content = textEmailTemplate
			.instance()
			.data("serverInfo", this.serverInfoBean)
			.data("serverUrlService", this.serverUrlService)
			.data("name", testUser.getUsername())
			.render();
		
		assertEquals(
			"\n" +
				"\n" +
				"\n" +
				"Hello " + testUser.getUsername() + ",\n" +
				"\n" +
				"No body!\n" +
				"\n" +
				"Thanks, Your Task Timekeeper Server\n" +
				"\n" +
				this.serverUrlService.getBaseServerUrl() + "\n" +
				"(Server run by " + this.serverInfoBean.getOrganization() + " - " + this.serverInfoBean.getOrgUrl()
																									   .get() + ")\n",
			content
		);
	}
}
