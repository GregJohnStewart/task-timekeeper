package com.gjs.taskTimekeeper.webServer.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.CorruptedKeyException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.IncorrectPasswordException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.PasswordValidationException;
import com.gjs.taskTimekeeper.webServer.server.validation.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.spec.EncryptablePasswordSpec;
import org.wildfly.security.password.spec.IteratedSaltedPasswordAlgorithmSpec;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@ApplicationScoped
public class PasswordService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordService.class);

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int ITERATIONS = 12;
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final PasswordFactory passwordFactory;
    private final PasswordValidator passwordValidator;

    public PasswordService(
            PasswordValidator passwordValidator
    ){
        this.passwordValidator = passwordValidator;
        WildFlyElytronPasswordProvider provider = WildFlyElytronPasswordProvider.getInstance();

        try {
            this.passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, provider);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Somehow got an exception when setting up password factory. Error: ", e);
            throw new RuntimeException(e);
        }
    }


    public String createPasswordHash(String password) throws PasswordValidationException {
        this.passwordValidator.validateAndSanitize(password);

        IteratedSaltedPasswordAlgorithmSpec iteratedAlgorithmSpec = new IteratedSaltedPasswordAlgorithmSpec(ITERATIONS, getSalt());
        EncryptablePasswordSpec encryptableSpec = new EncryptablePasswordSpec(password.toCharArray(), iteratedAlgorithmSpec);

        BCryptPassword original = null;
        try {
            original = (BCryptPassword) passwordFactory.generatePassword(encryptableSpec);
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Somehow got an invalid key spec. This should not happen. Error: ", e);
            throw new RuntimeException(e);
        }
        try {
            return ENCODER.encodeToString(
                    MAPPER.writeValueAsBytes(original)
            );
        } catch (JsonProcessingException e) {
            LOGGER.error("Somehow got a json processing exception. This should not happen. Error: ", e);
            throw new RuntimeException(e);
        }
    }

    public boolean passwordMatchesHash(String encodedPass, String pass) throws CorruptedKeyException{
        BCryptPassword original = null;
        try {
            original = MAPPER.readValue(DECODER.decode(encodedPass), BCryptPassword.class);
        } catch (IOException e) {
            LOGGER.error("Something went wrong in decoding the password. Is the key corrupt? Error: ", e);
            throw new CorruptedKeyException(e);
        }
        try {
            return passwordFactory.verify(original, pass.toCharArray());
        } catch (InvalidKeyException e) {
            LOGGER.error("Somehow got an invalid key. This could happen if given a corrupted key. Error: ", e);
            throw new CorruptedKeyException(e);
        }
    }

    public void assertPasswordMatchesHash(String encodedPass, String pass) throws IncorrectPasswordException, CorruptedKeyException {
        if(!this.passwordMatchesHash(encodedPass, pass)){
            throw new IncorrectPasswordException("Password given was incorrect.");
        }
    }

    /**
     * Gets a random salt.
     * @return A random salt
     */
    private static byte[] getSalt(){
        byte[] salt = new byte[BCryptPassword.BCRYPT_SALT_SIZE];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
}
