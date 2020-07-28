package com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.stats.stats.AllStats;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeManagerResponse {
	protected TimeManager timeManagerData;
	protected AllStats stats;
	protected Date lastUpdated;
}
