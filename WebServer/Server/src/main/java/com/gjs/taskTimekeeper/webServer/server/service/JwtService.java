package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.UserLevel;
import com.gjs.taskTimekeeper.webServer.server.utils.StaticUtils;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class JwtService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private final ServerInfoBean serverInfo;
    private final long defaultExpiration;
    private final long extendedExpiration;
    private final String sigKeyId;
    private final PrivateKey privateKey;


    public JwtService(
            ServerInfoBean serverInfo,
            @ConfigProperty(name="mp.jwt.verify.publickey.location")
                    String privateKeyLocation,
            @ConfigProperty(name="mp.jwt.expiration.default")
                    long defaultExpiration,
            @ConfigProperty(name="mp.jwt.expiration.extended")
                    long extendedExpiration
    ) throws Exception {
        this.serverInfo = serverInfo;
        this.defaultExpiration = defaultExpiration;
        this.extendedExpiration = extendedExpiration;
        this.sigKeyId = privateKeyLocation;
        this.privateKey = readPrivateKey(privateKeyLocation);
    }

    public String generateTokenString(
            User user,
            boolean extendedTimeout
    ){
        JwtClaimsBuilder claims = Jwt.claims(this.getUserClaims(user));

        long currentTimeInSecs = StaticUtils.currentTimeInSecs();
        long expirationTime = currentTimeInSecs + (extendedTimeout ? this.extendedExpiration : this.defaultExpiration);

        claims.issuedAt(currentTimeInSecs);
        claims.expiresAt(expirationTime);
        claims.claim(Claims.auth_time.name(), currentTimeInSecs);

        return claims.jws().signatureKeyId(this.sigKeyId).sign(this.privateKey);
    }

    private Map<String, Object> getUserClaims(User user){
        Map<String, Object> output = this.getBaseClaims();

        String userIdentification = user.id + ";" + user.getUsername() + ";" + user.getEmail();

        output.put("jti", user.id + "-" + user.getLastLogin().getTime() + "-" + user.getNumLogins());//TODO: move to utility, test
        output.put("sub", userIdentification);
        output.put("aud", userIdentification);
        output.put("upn", user.getEmail());
        output.put("userId", user.id);

        output.put("roleMappings", new HashMap<String, Object>());

        List<Object> groups = new ArrayList<>();

        groups.add(UserLevel.REGULAR.name());
        if(user.getLevel() == UserLevel.ADMIN){
            groups.add(UserLevel.ADMIN.name());
        }

        output.put("groups", groups);

        return output;
    }

    private Map<String, Object> getBaseClaims(){
        Map<String, Object> output = new HashMap<>();

        output.put("iss", serverInfo.getOrganization() + " - Task Timekeeper Server");

        return output;
    }

    /**
     * Read a PEM encoded private key from the classpath
     * TODO:: handle get from not classpath
     *
     * @param pemResName - key file resource name
     * @return PrivateKey
     * @throws Exception on decode failure
     */
    private static PrivateKey readPrivateKey(final String pemResName) throws Exception {
        LOGGER.info("Reading in private key.");

        URL url = JwtService.class.getClassLoader().getResource(pemResName);
        if(url == null){
            LOGGER.debug("Private key not in classpath.");
            url = new URL("file:" + pemResName);
        }else{
            LOGGER.debug("Private key in classpath.");
        }
        LOGGER.debug("Private key location: {}", url);
        String rawPem = IOUtils.toString(url, StandardCharsets.UTF_8);
        return decodePrivateKey(rawPem);
    }

    /**
     * Decode a PEM encoded private key string to an RSA PrivateKey
     *
     * @param pemEncoded - PEM string for private key
     * @return PrivateKey
     * @throws Exception on decode failure
     */
    public static PrivateKey decodePrivateKey(final String pemEncoded) throws Exception {
        byte[] encodedBytes = toEncodedBytes(pemEncoded);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private static byte[] toEncodedBytes(final String pemEncoded) {
        final String normalizedPem = removeBeginEnd(pemEncoded);
        return DECODER.decode(normalizedPem);
    }

    private static String removeBeginEnd(String pem) {
        pem = pem.replaceAll("-----BEGIN (.*)-----", "");
        pem = pem.replaceAll("-----END (.*)----", "");
        pem = pem.replaceAll("\r\n", "");
        pem = pem.replaceAll("\n", "");
        return pem.trim();
    }
}
