package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.Arrays;

import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingAssertions.assertUserTimeData;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class PeriodManagementTest extends TimekeepingUiTest {
	
	private TimeManager orig;
	private TimeManager origWithPeriods;
	
	public PeriodManagementTest(
		ServerInfoBean infoBean,
		WebDriverWrapper wrapper
	) {
		super(infoBean, wrapper);
		
		orig = new TimeManager(
			Arrays.asList(
				new Task("Test Task 01")
			)
		);
		
		
		Task testTask = new Task("Test Task 01");
		origWithPeriods = new TimeManager(
			Arrays.asList(testTask),
			Arrays.asList(new WorkPeriod(
				Arrays.asList(new Timespan(testTask))
			))
		);
	}
	
	@BeforeEach
	public void setup() throws InterruptedException {
		super.setup();
		
		try {
			this.setupUserData(orig);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		
		this.wrapper.waitForElement(By.id("periodsTab")).click();
	}
	
	//	@Test
	public void testAddPeriod() throws IOException, InterruptedException {
		WebElement periodAddButton = this.wrapper.waitForElement(By.id("addPeriodButton"));
		
		periodAddButton.click();
		this.wrapper.waitForAjaxComplete();
		
		orig.addWorkPeriod(new WorkPeriod());
		
		assertUserTimeData(
			this.testUser,
			this.wrapper,
			orig,
			this.userUtils.getTestUserJwt(this.testUser)
		);
		this.assertTimestampsUpdated();
	}
	
	
	//TODO:: period remove
	//	@Test
	public void testRemovePeriod() throws IOException, InterruptedException {
		this.setupUserData(origWithPeriods);
		this.wrapper.waitForElement(By.id("periodsTab")).click();
		
		//TODO:: remove one, adjust
		
		
		assertUserTimeData(
			this.testUser,
			this.wrapper,
			origWithPeriods,
			this.userUtils.getTestUserJwt(this.testUser)
		);
		//		this.assertTimestampsUpdated();
	}
	
	//TODO:: period select
}
