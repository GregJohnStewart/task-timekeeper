package com.gjs.taskTimekeeper.webServer.server.ui.timekeeping;

import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import com.gjs.taskTimekeeper.webServer.server.testResources.webUi.WebDriverWrapper;
import org.openqa.selenium.JavascriptExecutor;

import java.time.LocalDateTime;

public class TimekeepingHelpers {
	
	public static LocalDateTime getTimeDataLastDataLoad(WebDriverWrapper wrapper) {
		LocalDateTime output = null;
		
		while(output == null) {
			output = TimeParser.parse((String)((JavascriptExecutor)wrapper.getDriver()).executeScript(
				"return $('#lastDataLoadSpan').text();"));
		}
		
		return output;
	}
	
	public static LocalDateTime getTimeDataLastDataChange(WebDriverWrapper wrapper) {
		return TimeParser.parse((String)((JavascriptExecutor)wrapper.getDriver()).executeScript(
			"return $('#lastDataChangeSpan').text();"));
	}
}
