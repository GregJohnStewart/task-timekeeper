package com.gjs.taskTimekeeper.webServer.server.validation;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.UsernameValidationException;

import java.util.regex.Pattern;

public class UsernameValidator extends StringValidator {
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
            throw new UsernameValidationException("Username too short. Must be at least "+USERNAME_MIN_LENGTH+" character(s) long.");
        }
        if(trimmed.length() > USERNAME_MAX_LENGTH){
            throw new UsernameValidationException("Username too long. Can't be more than "+USERNAME_MAX_LENGTH+" characters long.");
        }
        if(WHITESPACE_PATTERN.matcher(trimmed).find()){
            throw new UsernameValidationException("Username cannot contain whitespace.");
        }
    }

    //TODO:: add validate/sanitize new user name
}
