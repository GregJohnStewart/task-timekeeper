package com.gjs.taskTimekeeper.webServer.server.testResources.entity;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;

public class UserUtils {

    public static User getTestUser(boolean persist, PasswordService passwordService){
        User testUser = new User();

        testUser.setEmail("testing@test.test");
        testUser.setUsername("helloWorld");
        testUser.setHashedPass(passwordService.createPasswordHash("1!HelloWorldasdfghjklqwertyuiop1"));

        if(persist){
            testUser.persist();
        }

        return testUser;
    }
}
