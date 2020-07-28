package com.gjs.taskTimekeeper.baseCode.stats;

import com.gjs.taskTimekeeper.baseCode.core.TimeKeeperException;

public class TimeManagerStatsException extends TimeKeeperException {
    public TimeManagerStatsException() {
    }
    
    public TimeManagerStatsException(String message) {
        super(message);
    }
    
    public TimeManagerStatsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public TimeManagerStatsException(Throwable cause) {
        super(cause);
    }
    
    public TimeManagerStatsException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
