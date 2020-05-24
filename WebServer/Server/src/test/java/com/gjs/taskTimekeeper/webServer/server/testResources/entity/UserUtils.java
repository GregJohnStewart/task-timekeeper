package com.gjs.taskTimekeeper.webServer.server.testResources.entity;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.JwtService;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.service.TokenService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import java.util.Date;

@Getter
@Dependent
public class UserUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserUtils.class);
    private static final String testUserEmailFormat = "testUser%03d@test.test";
    private static final String testUserNameFormat = "testUser%03d";
    public static final String TEST_USER_PASSWORD = "aA1!0000000000000000000000000001";

    private static int userCount = 0;

    public static synchronized int getCurUserCount(){
        return ++userCount;
    }

    private String testUserEmail;
    private String testUserPassword;
    private String testUserPasswordHash;

    private final JwtService jwtService;

    public UserUtils(JwtService jwtService,PasswordService passwordService, TokenService tokenService) {
        this.jwtService = jwtService;
        this.testUserPassword = tokenService.generateToken();
        this.testUserPasswordHash = passwordService.createPasswordHash(this.testUserPassword);
    }

    public User getTestUser(){
        return User.findByEmail(this.getTestUserEmail());
    }

    public User setupTestUser(boolean persist){
        if(this.testUserPassword == null){
            throw new IllegalStateException("Test user password not setup.");
        }

        int curUserCount = getCurUserCount();
        User testUser = new User();

        testUser.setEmail(String.format(testUserEmailFormat, curUserCount));
        testUser.setUsername(String.format(testUserNameFormat, curUserCount));
        testUser.setHashedPass(this.testUserPasswordHash);

        testUser.setLastLogin(new Date());

        LOGGER.info("Creating test user No {}", curUserCount);
        LOGGER.info("Username: {}", testUser.getUsername());
        LOGGER.info("   Email: {}", testUser.getEmail());

        try{
            User.findByEmail(testUser.getEmail());
            throw new IllegalStateException("Test user "+testUser.getUsername()+" already exists.");
        }catch (EntityNotFoundException e){
            //what we want
        }

        if(persist){
            testUser.persist();
        }

        this.testUserEmail = testUser.getEmail();

        return testUser;
    }

    public String getTestUserJwt(){
        return jwtService.generateTokenString(this.getTestUser(), false);
    }
}
