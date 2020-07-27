package com.gjs.taskTimekeeper.webServer.server.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

/**
 * Bean to use to check if we are using the prepackaged keys or not.
 * <p>
 * TODO:: test
 */
@ApplicationScoped
public class DefaultKeysChecker {
	private static final String[] PACKAGED_KEYWORDS = new String[]{
		"packagedPrivateKey",
		"packagedPublicKey"
	};
	
	private final String[] keyLocations;
	
	public DefaultKeysChecker(
		@ConfigProperty(name = "mp.jwt.verify.privatekey.location")
			String privateKeyLocation,
		@ConfigProperty(name = "mp.jwt.verify.publickey.location")
			String publicKeyLocation
	) {
		this.keyLocations = new String[]{
			privateKeyLocation,
			publicKeyLocation
		};
	}
	
	public boolean usingPackagedKeys() {
		return anyMatch(
			this.keyLocations,
			PACKAGED_KEYWORDS
		);
	}
	
	private boolean anyMatch(String[] strings, String[] toLookFor) {
		for(String curString : strings) {
			for(String curLookingFor : toLookFor) {
				if(curString.contains(curLookingFor)) {
					return true;
				}
			}
		}
		return false;
	}
}
