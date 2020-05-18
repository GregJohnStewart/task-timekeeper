package com.gjs.taskTimekeeper.webServer.server.validation;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.EmailValidationException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.testResources.RunningServerTest;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestMongo;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(TestMongo.class)
class EmailValidatorDbBasedTest extends RunningServerTest {
    private EmailValidator validator = new EmailValidator();

    @Test
    public void addNewUserNameTest(){
        //add initial test user
        User testUser = new User();
        testUser.setEmail(validator.validateAndSanitize("user01addNewUserNameTest@hello.world"));
        testUser.persist();

        //check
        Assertions.assertDoesNotThrow(() -> {
            this.validator.validateSanitizeAssertDoesntExist("user02addNewUserNameTest@hello.world");
        });
    }

    @Test
    public  void addNewUserDuplicateEmailTest(){
        //add initial test user
        User testUser = new User();
        testUser.setEmail(validator.validateAndSanitize("user01addNewUserDuplicateEmailTest@hello.world"));
        testUser.persist();

        //check
        Assertions.assertThrows(EmailValidationException.class, () -> {
            this.validator.validateSanitizeAssertDoesntExist("user01addNewUserDuplicateEmailTest@hello.world");
        });
    }
}