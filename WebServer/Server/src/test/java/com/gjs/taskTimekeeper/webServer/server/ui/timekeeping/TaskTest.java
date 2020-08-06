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
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebAssertions.submitFormAndAssertElementsInvalid;
import static com.gjs.taskTimekeeper.webServer.server.testResources.webUi.form.FormHelpers.submitForm;
import static com.gjs.taskTimekeeper.webServer.server.ui.timekeeping.TimekeepingAssertions.assertLastDataChangeNotUpdated;
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
			
			assertLastDataLoadAndChangeNotUpdated(
				this.lastDataLoad,
				this.lastDataChange,
				this.wrapper
			);
		}
		
		
		{
			taskNameField.sendKeys("Test Task");
			
			submitForm(taskAddEditForm);
			this.wrapper.waitForAjaxComplete();
			this.wrapper.waitForModalClose();
			
			assertUserTimeData(
				this.testUser,
				this.wrapper,
				new TimeManager(new TreeSet<>(Arrays.asList(new Task("Test Task")))),
				this.userUtils.getTestUserJwt(this.testUser)
			);
			this.assertTimestampsUpdated();
		}
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
			this.wrapper.waitForModalClose();
			
			assertUserTimeData(
				this.testUser,
				this.wrapper,
				new TimeManager(Arrays.asList(new Task("Test Task", new HashMap<String, String>() {{
					put("some", "att");
				}}))),
				this.userUtils.getTestUserJwt(this.testUser)
			);
		}
		
		this.assertTimestampsUpdated();
	}
	
	//TODO:: edit test
	@Test
	public void remTaskTest() throws IOException, InterruptedException {
		TimeManager manager = new TimeManager(
			new TreeSet<>(
				Arrays.asList(
					new Task("Test Task 01"),
					new Task("Test Task 02"),
					new Task("Test Task 03"),
					new Task("Test Task 04")
				)
			)
		);
		this.setupUserData(manager);
		this.wrapper.waitForElement(By.id("tasksTab")).click();
		
		WebElement taskTable = wrapper.waitForElement(By.id("tasksTableContent"));
		
		WebElement taskRow = taskTable.findElement(By.xpath("//*[contains(text(),'Test Task 03')]"))
									  .findElement(By.xpath("./.."));
		
		taskRow.findElement(By.className("taskRemoveButton")).click();
		
		{
			Alert alert = this.wrapper.getWait().until(ExpectedConditions.alertIsPresent());
			
			alert.dismiss();
			
			assertLastDataChangeNotUpdated(this.lastDataChange, this.wrapper);
		}
		
		taskRow.findElement(By.className("taskRemoveButton")).click();
		Alert alert = this.wrapper.getWait().until(ExpectedConditions.alertIsPresent());
		
		alert.accept();
		this.wrapper.waitForAjaxComplete();
		
		assertUserTimeData(
			this.testUser,
			this.wrapper,
			new TimeManager(
				new TreeSet<>(
					Arrays.asList(
						new Task("Test Task 01"),
						new Task("Test Task 02"),
						new Task("Test Task 04")
					)
				)
			),
			this.userUtils.getTestUserJwt(this.testUser)
		);
		this.assertTimestampsUpdated();
	}
}
