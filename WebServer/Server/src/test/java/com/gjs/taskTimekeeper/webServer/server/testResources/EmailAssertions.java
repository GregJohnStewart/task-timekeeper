package com.gjs.taskTimekeeper.webServer.server.testResources;

import io.quarkus.mailer.MockMailbox;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailAssertions {
	public final MockMailbox mailbox;
	
	public EmailAssertions(MockMailbox mailbox) {
		this.mailbox = mailbox;
	}
}
