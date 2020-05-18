package com.gjs.taskTimekeeper.webServer.webLibrary.timeManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WholeTimeManagerUpdateRequest {
    private byte[] timeManagerData;
}
