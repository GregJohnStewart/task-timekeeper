package com.gjs.taskTimekeeper.webServer.server.testResources.entity;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.JwtService;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.service.TokenService;
import lombok.Getter;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;

@Getter
@ApplicationScoped
public class UserUtils {

    private final String testUserEmail = "testing@test.test";
    private final String testUserUsername = "helloWorld";
    private String testUserPasswordHash;
    private String testUserPassword;

    @Inject
    private JwtService jwtService;

    @PostConstruct
    public void setTestUserPassword(PasswordService passwordService, TokenService tokenService){
        this.testUserPassword = tokenService.generateToken();
        this.testUserPasswordHash = passwordService.createPasswordHash(this.testUserPassword);
    }

    public User getTestUser(boolean persist){
        User testUser = new User();

        testUser.setEmail(this.getTestUserEmail());
        testUser.setUsername(this.getTestUserUsername());
        testUser.setHashedPass(this.testUserPasswordHash);

        testUser.setLastLogin(new Date());

        if(persist){
            testUser.persist();
        }

        return testUser;
    }

    public String getTestUserJwt(){
        return jwtService.generateTokenString(User.findByEmail(this.getTestUserEmail()), false);
    }
}
