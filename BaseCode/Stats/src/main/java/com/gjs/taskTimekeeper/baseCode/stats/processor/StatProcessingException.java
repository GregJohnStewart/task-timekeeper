package com.gjs.taskTimekeeper.baseCode.stats.processor;

import com.gjs.taskTimekeeper.baseCode.stats.TimeManagerStatsException;

public class StatProcessingException extends TimeManagerStatsException {
	
	public StatProcessingException() {
	}
	
	public StatProcessingException(String message) {
		super(message);
	}
	
	public StatProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public StatProcessingException(Throwable cause) {
		super(cause);
	}
	
	public StatProcessingException(
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
