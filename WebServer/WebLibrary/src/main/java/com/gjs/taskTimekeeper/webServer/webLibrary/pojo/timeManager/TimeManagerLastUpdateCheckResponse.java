package com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeManagerLastUpdateCheckResponse {
	private Date lastUpdate;
}
