package com.gjs.taskTimekeeper.webServer.server.validation.validate;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.UsernameValidationException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.regex.Pattern;

@ApplicationScoped
public class UsernameValidator extends StringValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsernameValidator.class);
    public static final int USERNAME_MAX_LENGTH = 20;
    public static final int USERNAME_MIN_LENGTH = 1;
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    @Override
    protected void validate(String object) throws UsernameValidationException {
        if(object == null){
            throw new UsernameValidationException("Username cannot be null.");
        }
        String trimmed = object.trim();

        if(trimmed.length() < USERNAME_MIN_LENGTH){
            LOGGER.warn("Username given too short. Length: {}, given: {}", trimmed.length(), trimmed);
            throw new UsernameValidationException("Username too short. Must be at least "+USERNAME_MIN_LENGTH+" character(s) long.");
        }
        if(trimmed.length() > USERNAME_MAX_LENGTH){
            LOGGER.warn("Username given too long. Length: {}, given: {}", trimmed.length(), trimmed);
            throw new UsernameValidationException("Username too long. Can't be more than "+USERNAME_MAX_LENGTH+" characters long.");
        }
        if(WHITESPACE_PATTERN.matcher(trimmed).find()){
            LOGGER.warn("Username has whitespace, given: \"{}\"", trimmed);
            throw new UsernameValidationException("Username cannot contain whitespace.");
        }
    }

    public void assertDoesntExist(String validatedSanitized){
        try {
            User.findByUsername(validatedSanitized);
            throw new UsernameValidationException("Username already exists.");
        } catch (EntityNotFoundException e){
            // nothing to do
        }
    }

    public String validateSanitizeAssertDoesntExist(String object){
        String validated = this.validateAndSanitize(object);
        this.assertDoesntExist(object);
        return validated;
    }

}
