package com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    private String user;
    private String plainPass;
    private boolean extendedTimeout = false;
}
