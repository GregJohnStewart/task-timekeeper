package com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.action;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeManagerActionRequest {
	private ActionConfig actionConfig;
	private Integer selectedPeriod;
}
