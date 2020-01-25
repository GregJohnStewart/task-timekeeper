package com.gjs.taskTimekeeper.webServer.server.validation;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.PasswordValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.regex.Pattern;

@ApplicationScoped
public class PasswordValidator extends StringValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordValidator.class);

    private final Pattern validationPattern;

    public PasswordValidator(String pattern){
        validationPattern = Pattern.compile(pattern);
    }

    @Override
    protected String sanitize(String object) {
        //don't do anything to passwords
        return object;
    }

    @Override
    protected void validate(String object) {
        if(!validationPattern.matcher(object).matches()){
            throw new PasswordValidationException("Password does not meet requirements.");
        }
    }
}
