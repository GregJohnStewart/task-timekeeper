package com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.action;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.stats.stats.AllStats;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.whole.WholeTimeManagerUpdateResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor()
@NoArgsConstructor
public class TimeManagerActionResponse extends WholeTimeManagerUpdateResponse {
	private String regOut;
	private String errOut;
	
	public TimeManagerActionResponse(
		TimeManager timeManagerData,
		AllStats stats,
		Date lastUpdated,
		boolean changed,
		String regOut,
		String errOut
	) {
		super(timeManagerData, stats, lastUpdated, changed);
		this.regOut = regOut;
		this.errOut = errOut;
	}
}
