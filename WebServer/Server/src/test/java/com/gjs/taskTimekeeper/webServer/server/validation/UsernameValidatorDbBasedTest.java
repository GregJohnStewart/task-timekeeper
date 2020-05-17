package com.gjs.taskTimekeeper.webServer.server.validation;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.UsernameValidationException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
class UsernameValidatorDbBasedTest extends RunningServerTest {
    private UsernameValidator validator = new UsernameValidator();

    @Test
    public void addNewUserNameTest(){
        //add initial test user
        User testUser = new User();
        testUser.setUsername(validator.validateAndSanitize("user01UsernameTest"));
        testUser.persist();

        //check
        Assertions.assertDoesNotThrow(() -> {
            this.validator.validateSanitizeAssertDoesntExist("user02UsernameTest");
        });
    }

    @Test
    public  void addNewUserDuplicateNameTest(){
        //add initial test user
        User testUser = new User();
        testUser.setUsername(validator.validateAndSanitize("user01UsernameTest"));
        testUser.persist();

        //check
        Assertions.assertThrows(UsernameValidationException.class, () -> {
            this.validator.validateSanitizeAssertDoesntExist("user01UsernameTest");
        });
    }
}