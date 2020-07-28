package com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.whole;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.stats.stats.AllStats;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.TimeManagerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WholeTimeManagerUpdateResponse extends TimeManagerResponse {
	protected boolean changed;
	
	public WholeTimeManagerUpdateResponse(
		TimeManager timeManagerData,
		AllStats stats,
		Date lastUpdated,
		boolean changed
	) {
		super(timeManagerData, stats, lastUpdated);
		this.setChanged(changed);
	}
}
