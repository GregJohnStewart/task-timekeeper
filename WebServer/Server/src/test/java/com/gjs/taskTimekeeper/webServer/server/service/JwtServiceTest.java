package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import com.gjs.taskTimekeeper.webServer.server.testResources.entity.UserUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
public class JwtServiceTest extends RunningServerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceTest.class);

    @Inject
    private JwtService jwtService;

    @Inject PasswordService passwordService;

    private User testUser;

    @BeforeEach
    public void setTestUser(){
        this.testUser = UserUtils.getTestUser(true, passwordService);
    }


    @Test
    public void test(){
        this.testUser.setLastLogin(new Date());
        this.testUser.setNumLogins(1L);

        String jwt = jwtService.generateTokenString(this.testUser, false);

        LOGGER.info("Created test user's jwt: {}", jwt);

        assertNotNull(jwt);
    }

}
