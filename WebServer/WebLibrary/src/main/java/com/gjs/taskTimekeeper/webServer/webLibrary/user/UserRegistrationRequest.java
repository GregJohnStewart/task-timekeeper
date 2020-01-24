package com.gjs.taskTimekeeper.webServer.webLibrary.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {
    public String username;
    public String email;
    public String plainPassword;
}
