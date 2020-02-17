package com.gjs.taskTimekeeper.webServer.server.toMoveToLib;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationResponse {
    private String username;
    private String email;
    private String id;
}
