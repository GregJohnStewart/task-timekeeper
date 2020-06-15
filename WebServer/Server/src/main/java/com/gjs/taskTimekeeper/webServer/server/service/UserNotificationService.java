package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.api.ResourcePath;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class UserNotificationService {
	
	private final MailTemplate welcomeEmailTemplate;
	
	public UserNotificationService(
		@ResourcePath("email/notification/accountChangeNotification")
			MailTemplate welcomeEmailTemplate
	) {
		this.welcomeEmailTemplate = welcomeEmailTemplate;
	}
	
	
	public CompletionStage<Void> alertToAccountChange(
		User user,
		String change,
		String details
	) {
		return this.welcomeEmailTemplate
			.to(user.getEmail())
			.subject("TaskTimekeeper Server account information changed.")
			.data("username", user.getUsername())
			.data("change", change)
			.data("details", details)
			.send();
	}
}
