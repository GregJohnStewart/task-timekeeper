package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.CorruptedKeyException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.IncorrectPasswordException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.PasswordValidationException;
import com.gjs.taskTimekeeper.webServer.server.validation.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.common.codec.DecodeException;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.spec.EncryptablePasswordSpec;
import org.wildfly.security.password.spec.IteratedSaltedPasswordAlgorithmSpec;
import org.wildfly.security.password.util.ModularCrypt;

import javax.enterprise.context.ApplicationScoped;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * https://www.javatips.net/api/wildfly-security-master/wildfly-elytron-master/src/test/java/org/wildfly/security/password/impl/BCryptPasswordTest.java
 */
@ApplicationScoped
public class PasswordService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordService.class);

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String ALGORITHM = BCryptPassword.ALGORITHM_BCRYPT;
    private static final int ITERATIONS = 12;

    private final PasswordFactory passwordFactory;
    private final PasswordValidator passwordValidator;

    public PasswordService(
            PasswordValidator passwordValidator
    ){
		this.passwordValidator = passwordValidator;
		WildFlyElytronPasswordProvider provider = WildFlyElytronPasswordProvider.getInstance();
	
		try {
			this.passwordFactory = PasswordFactory.getInstance(ALGORITHM, provider);
		} catch(NoSuchAlgorithmException e) {
			LOGGER.error("Somehow got an exception when setting up password factory. Error: ", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Validate/sanitizes the password given, and makes a hash out of it.
	 *
	 * @param password The password to hash
	 * @return The hash for the password
	 * @throws PasswordValidationException If the password was invalid
	 */
	public String createPasswordHash(String password) throws PasswordValidationException {
		this.passwordValidator.validateAndSanitize(password);
		
		IteratedSaltedPasswordAlgorithmSpec iteratedAlgorithmSpec = new IteratedSaltedPasswordAlgorithmSpec(
			ITERATIONS,
			getSalt()
		);
		EncryptablePasswordSpec encryptableSpec = new EncryptablePasswordSpec(
			password.toCharArray(),
			iteratedAlgorithmSpec
		);
		
		try {
			BCryptPassword original = (BCryptPassword)passwordFactory.generatePassword(encryptableSpec);
			return ModularCrypt.encodeAsString(original);
		} catch(InvalidKeySpecException e) {
            LOGGER.error("Somehow got an invalid key spec. This should not happen. Error: ", e);
            throw new WebServerException(e);
        }
    }

    public boolean passwordMatchesHash(String encodedPass, String pass) throws CorruptedKeyException{
        BCryptPassword original = null;
        try {
            original = (BCryptPassword) passwordFactory.translate(ModularCrypt.decode(encodedPass));
        } catch (DecodeException e) {
            LOGGER.error("Was unable to decode the password. Error: ", e);
            throw new CorruptedKeyException(e);
        } catch (InvalidKeySpecException | InvalidKeyException e) {
            LOGGER.error("Somehow got an invalid key/spec. This should not happen. Error: ", e);
            throw new WebServerException(e);
        }
        try {
            return passwordFactory.verify(original, pass.toCharArray()); // throws the invalid key exception
        } catch (InvalidKeyException e) {
            LOGGER.error("Somehow got an invalid key. This probably shouldn't happen? Error: ", e);
            throw new WebServerException(e);
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
