package com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.whole;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WholeTimeManagerUpdateRequest {
	private TimeManager timeManagerData;
}
