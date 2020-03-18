package com.gjs.taskTimekeeper.webServer.server.toMoveToLib;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    private String user;
    private String plainPass;
}
