package com.gjs.taskTimekeeper.desktopApp.config.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringPlaceholder {
	public static final DateTimeFormatter DATE_FORMATTER =
		DateTimeFormatter.ofPattern("dd-MM-YYYY");
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH-mm");
	public static final DateTimeFormatter DATETIME_FORMATTER =
		DateTimeFormatter.ofPattern("HH-mm_dd-MM-YYYY");
	
	public static String processPlaceholders(String string) {
		String output = string;
		
		LocalDateTime now = LocalDateTime.now();
		
		output = output.replaceAll("\\{HOME}", System.getProperty("user.home"));
		output = output.replaceAll("\\{DATE}", now.format(DATE_FORMATTER));
		output = output.replaceAll("\\{TIME}", now.format(TIME_FORMATTER));
		output = output.replaceAll("\\{DATETIME}", now.format(DATETIME_FORMATTER));
		
		return output;
	}
}
