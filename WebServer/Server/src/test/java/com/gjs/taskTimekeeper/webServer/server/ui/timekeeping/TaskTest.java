package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions.submitFormAndAssertElementsInvalid;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.form.FormHelpers.submitForm;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingAssertions.assertLastDataLoadAndChangeNotUpdated;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingAssertions.assertUserTimeData;


@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
public class TaskTest extends TimekeepingUiTest {
	public TaskTest(
		ServerInfoBean infoBean,
		WebDriverWrapper wrapper
	) {
		super(infoBean, wrapper);
	}
	
	@BeforeEach
	public void setup() throws InterruptedException {
		super.setup();
		
		this.wrapper.waitForElement(By.id("tasksTab")).click();
	}
	
	@Test
	public void addTaskTest() throws IOException, InterruptedException {
		WebElement addTaskButton = this.wrapper.waitForElement(By.id("addTaskButton"));
		WebElement taskAddEditForm = this.wrapper.waitForElement(By.id("taskAddEditModalForm"));
		
		addTaskButton.click();
		
		WebElement taskNameField = this.wrapper.waitForElement(By.id("taskAddEditModalNameInput"));
		WebElement closeModalButton = taskAddEditForm.findElement(By.className("closeModalButton"));
		{
			
			submitFormAndAssertElementsInvalid(
				"id",
				(RemoteWebElement)taskAddEditForm,
				"taskAddEditModalNameInput"
			);
			
			closeModalButton.click();
			this.wrapper.waitForModalClose();
			assertLastDataLoadAndChangeNotUpdated(
				this.lastDataLoad,
				this.lastDataChange,
				this.wrapper
			);
		}
		
		addTaskButton.click();
		
		{
			taskNameField.sendKeys("Test Task");
			
			submitForm(taskAddEditForm);
			this.wrapper.waitForAjaxComplete();
			
			assertUserTimeData(this.testUser, new TimeManager(new TreeSet<>(Arrays.asList(new Task("Test Task")))));
			this.assertTimestampsUpdated();
		}
		//TODO:: assert task in list
	}
	
	@Test
	public void addTaskWithAttsTest() throws IOException, InterruptedException {
		WebElement addTaskButton = this.wrapper.waitForElement(By.id("addTaskButton"));
		WebElement taskAddEditForm = this.wrapper.waitForElement(By.id("taskAddEditModalForm"));
		
		addTaskButton.click();
		
		WebElement taskNameField = this.wrapper.waitForElement(By.id("taskAddEditModalNameInput"));
		WebElement closeModalButton = taskAddEditForm.findElement(By.className("closeModalButton"));
		WebElement addAttributeButton = taskAddEditForm.findElement(By.id("taskAddEditModalAddAttButton"));
		taskNameField.sendKeys("Test Task");
		
		addAttributeButton.click();
		WebElement attNameInput = taskAddEditForm.findElement(By.className("attNameInput"));
		WebElement attValueInput = taskAddEditForm.findElement(By.className("attValueInput"));
		
		{
			submitFormAndAssertElementsInvalid(
				"class",
				(RemoteWebElement)taskAddEditForm,
				"attNameInput"
			);
		}
		
		
		{
			attNameInput.sendKeys("some");
			attValueInput.sendKeys("att");
			
			submitForm(taskAddEditForm);
			this.wrapper.waitForAjaxComplete();
			
			assertUserTimeData(
				this.testUser,
				new TimeManager(Arrays.asList(new Task("Test Task", new HashMap<String, String>() {{
					put("some", "att");
				}})))
			);
		}
		
		this.assertTimestampsUpdated();
		
		//TODO:: assert task in list
	}
}
