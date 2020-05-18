package com.gjs.taskTimekeeper.webServer.webLibrary.timeManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WholeTimeManagerUpdateResponse {
    private byte[] timeManagerData;
    private Date lastUpdated;
    private boolean changed;
}
