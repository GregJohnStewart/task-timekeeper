package com.gjs.taskTimekeeper.webServer.server.testResources.entity;

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
    
    public static synchronized int getCurUserCount() {
        return ++userCount;
    }
    
    private final JwtService jwtService;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    
    public UserUtils(JwtService jwtService, PasswordService passwordService, TokenService tokenService) {
        this.jwtService = jwtService;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }
    
    public TestUser setupTestUser() {
        int curUserCount = getCurUserCount();
        
        TestUser output = new TestUser(
            this,
            String.format(testUserEmailFormat, curUserCount),
            String.format(testUserNameFormat, curUserCount),
            this.tokenService.generateToken()
        );
        
        LOGGER.info("Creating test user No {}", curUserCount);
        LOGGER.info("Username: {}", output.getUsername());
        LOGGER.info("   Email: {}", output.getEmail());
        LOGGER.info("Password: {}", output.getPlainPassword());
        
        return output;
    }
    
    public TestUser setupTestUser(boolean persist) {
        TestUser testUser = this.setupTestUser();
        if(persist) {
            setupNewUser(testUser, true);
        }
        return testUser;
    }
    
    public User setupNewUser(TestUser testUser, boolean persist) {
        User newUser = new User();
        
        newUser.setEmail(testUser.getEmail());
        newUser.setUsername(testUser.getUsername());
        newUser.setHashedPass(this.passwordService.createPasswordHash(testUser.getPlainPassword()));
        
        newUser.setLastLogin(new Date());
        
        if(persist) {
            newUser.persist();
        }
        
        return newUser;
    }
    
    public String getTestUserJwt(User testUser) {
        if(!testUser.isPersisted()) {
            throw new IllegalStateException("Can't get jwt for user that is not persisted.");
        }
        return jwtService.generateTokenString(testUser, false);
    }
    
    public String getTestUserJwt(TestUser testUser) {
        return this.getTestUserJwt(testUser.getUserObj());
    }
}
