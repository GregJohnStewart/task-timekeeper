package com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.whole;

import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WholeTimeManagerResponse {
    private TimeManager timeManagerData;
    private Date lastUpdate;
}
