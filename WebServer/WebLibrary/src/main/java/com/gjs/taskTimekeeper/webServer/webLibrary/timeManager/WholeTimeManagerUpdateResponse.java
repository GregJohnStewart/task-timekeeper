package com.gjs.taskTimekeeper.webServer.webLibrary.timeManager;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WholeTimeManagerUpdateResponse {
	private TimeManager timeManagerData;
	private Date lastUpdated;
    private boolean changed;
}
