package com.gjs.taskTimekeeper.webServer.server.testResources;

import com.gjs.taskTimekeeper.webServer.server.testResources.entity.UserUtils;
import io.quarkus.mailer.MockMailbox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.inject.Inject;

@Execution(ExecutionMode.SAME_THREAD)//TODO:: remove this, when know how to make this work
public abstract class RunningServerTest extends WebServerTest {
	@Inject
	protected MockMailbox mailbox;
	@Inject
	protected UserUtils userUtils;
	
	@BeforeEach
	public void beforeEach() {
		this.cleanup();
	}
	
	@AfterEach
	public void afterEach() {
		this.cleanup();
	}
	
	public void cleanup() {
	
	}
	
	public void cleanupDatabaseAndMail() {
		this.mailbox.clear();
		TestResourceLifecycleManager.cleanMongo();
	}
}
