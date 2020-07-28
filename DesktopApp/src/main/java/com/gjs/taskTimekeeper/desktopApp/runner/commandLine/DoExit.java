package com.gjs.taskTimekeeper.desktopApp.runner.commandLine;

import com.gjs.taskTimekeeper.desktopApp.DesktopAppException;

public class DoExit extends DesktopAppException {
	public DoExit() {
	}
	
	public DoExit(String message) {
		super(message);
	}
	
	public DoExit(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DoExit(Throwable cause) {
		super(cause);
	}
	
	public DoExit(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
