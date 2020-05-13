package com.gjs.taskTimekeeper.webServer.server.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

/**
 * Bean to use to check if we are using the prepackaged keys or not.
 *
 * TODO:: test
 */
@ApplicationScoped
public class DefaultKeysChecker{
    private static final String PACKAGED_PRIVATE = "packagedPrivateKey.pem";
    private static final String PACKAGED_PUBLIC = "packagedPublicKey.pem";

    String privateKeyLocation;
    String publicKeyLocation;

    public DefaultKeysChecker(
            @ConfigProperty(name="mp.jwt.verify.privatekey.location")
                    String privateKeyLocation,
            @ConfigProperty(name="mp.jwt.verify.publickey.location")
                    String publicKeyLocation
    ){
        this.privateKeyLocation = privateKeyLocation;
        this.publicKeyLocation = publicKeyLocation;
    }

    public boolean usingPackagedKeys(){
        return this.privateKeyLocation.contains(PACKAGED_PRIVATE) ||
                this.publicKeyLocation.contains(PACKAGED_PUBLIC);
    }

}
