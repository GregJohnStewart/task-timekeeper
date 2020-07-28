package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.exception.validation.ValidationException;
import com.gjs.taskTimekeeper.webServer.server.validation.validate.PasswordValidator;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;

import javax.enterprise.context.ApplicationScoped;
import java.security.SecureRandom;

/**
 * Service to generate tokens, made to be used in conjunction with the password service.
 */
@ApplicationScoped
public class TokenService {
	private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
	private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DIGITS = "0123456789";
	private static final String SPECIAL = "-._~";//url safe only
	
	private final PasswordValidator passwordValidator;
	private SecureRandom secureRandom;
	
	private long numTokens = 0;
	private long numIterations = 0;
	
	public TokenService(PasswordValidator passwordValidator) {
		this.passwordValidator = passwordValidator;
		this.secureRandom = new SecureRandom();
	}
	
	/**
	 * Gets a number between the two given numbers, inclusive of both
	 *
	 * @param min The minimum possible number
	 * @param max The maximum possible number
	 * @return A number between the two given numbers, inclusive of both
	 */
	private int getBetween(int min, int max) {
		if(min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		
		return this.secureRandom.nextInt((max - min) + 1) + min;
	}
	
	/**
	 * Generates a token for use around the service.
	 *
	 * @return A token that will validate with the password service.
	 */
	public String generateToken() {
		int maxLen = this.passwordValidator.getMinLength() * 2;
		String bank = LOWER;
		String token = null;
		boolean valid = false;
		do {
			int len = getBetween(this.passwordValidator.getMinLength(), maxLen);
			StringBuilder sb = new StringBuilder();
			
			for(int i = 0; i < len; i++) {
				this.numIterations++;
				int choice = this.getBetween(0, 3);
				if(choice == 0) {
					bank = LOWER;
				}
				if(choice == 1) {
					bank = UPPER;
				}
				if(choice == 2) {
					bank = DIGITS;
				}
				if(choice == 3) {
					bank = SPECIAL;
				}
				sb.append(
					bank.charAt(
						this.getBetween(
							0,
							bank.length() - 1
						)
					)
				);
			}
			token = sb.toString();
			
			try {
				this.passwordValidator.validateAndSanitize(token);
				valid = true;
			} catch(ValidationException e) {
			
			}
		} while(!valid);
		this.numTokens++;
		return token;
	}
	
	@Gauge(name = "numTokensGenerated", unit = MetricUnits.NONE, description = "The number of tokens generated.")
	public Long numTokensGenerated() {
		return this.numTokens;
	}
	
	@Gauge(name = "numTotalIterations",
		   unit = MetricUnits.NONE,
		   description = "The number of iterations it took to generate token.")
	public Long numTotalIterations() {
		return this.numIterations;
	}
	
	@Gauge(name = "averageIterationsPerToken",
		   unit = MetricUnits.NONE,
		   description = "The average number of iterations it takes to make a token.")
	public Long averageIterationsPerToken() {
		return this.numIterations / this.numTokens;
	}
}
