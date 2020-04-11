package com.gjs.taskTimekeeper.webServer.server.testResources.entity;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.service.TokenService;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@Getter
@ApplicationScoped
public class UserUtils {

    private String testUserPasswordHash;
    private String testUserPassword;

    @PostConstruct
    public void setTestUserPassword(PasswordService passwordService, TokenService tokenService){
        this.testUserPassword = tokenService.generateToken();
        this.testUserPasswordHash = passwordService.createPasswordHash(this.testUserPassword);
    }

    public User getTestUser(boolean persist){
        User testUser = new User();

        testUser.setEmail("testing@test.test");
        testUser.setUsername("helloWorld");
        testUser.setHashedPass(this.testUserPasswordHash);

        if(persist){
            testUser.persist();
        }

        return testUser;
    }
}
