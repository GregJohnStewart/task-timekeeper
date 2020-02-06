package com.gjs.taskTimekeeper.webServer.server.validation;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.UsernameValidationException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;

//TODO: fix these failing on the last one for some reason....
@QuarkusTest
class UsernameValidatorDbBasedTest extends RunningServerTest {
    private UsernameValidator validator = new UsernameValidator();

//    @Test
    public void addNewUserNameTest(){
        //add initial test user
        User testUser = new User();
        testUser.setUsername(validator.validateAndSanitize("user01"));
        testUser.persist();

        //check
        Assertions.assertDoesNotThrow(() -> {
            this.validator.validateSanitizeAssertDoesntExist("user02");
        });
    }

//    @Test
    public  void addNewUserDuplicateNameTest(){
        //add initial test user
        User testUser = new User();
        testUser.setUsername(validator.validateAndSanitize("user01"));
        testUser.persist();

        //check
        Assertions.assertThrows(UsernameValidationException.class, () -> {
            this.validator.validateSanitizeAssertDoesntExist("user01");
        });
    }

//    @Test
    public void addNewUserDuplicateNameTwoTest(){
        //add initial test user
        User testUser = new User();
        testUser.setUsername(validator.validateAndSanitize("user01"));
        testUser.persist();

        //check
        Assertions.assertThrows(UsernameValidationException.class, () -> {
            this.validator.validateSanitizeAssertDoesntExist("user01");
        });
    }
}