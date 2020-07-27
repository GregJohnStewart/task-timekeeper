package com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.notification.NotificationMethods.EMAIL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettings {
	@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
	public List<NotificationMethods> accountChange = Arrays.asList(EMAIL);
}
