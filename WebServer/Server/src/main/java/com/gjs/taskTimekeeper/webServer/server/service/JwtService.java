package com.gjs.taskTimekeeper.webServer.server.service;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.utils.StaticUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserLevel;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import javax.enterprise.context.ApplicationScoped;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class JwtService {
	private static final Base64.Decoder DECODER = Base64.getDecoder();
	public static final String JWT_USER_ID_CLAIM = "userId";
	public static final String[] ROLES = {UserLevel.REGULAR.name(), UserLevel.ADMIN.name()};
	
	private final long defaultExpiration;
	private final long extendedExpiration;
	private final String sigKeyId;
	private final String issuer;
	private final PrivateKey privateKey;
	
	public JwtService(
		ServerInfoBean serverInfo,
		@ConfigProperty(name = "mp.jwt.verify.privatekey.location")
			String privateKeyLocation,
		@ConfigProperty(name = "mp.jwt.expiration.default")
			long defaultExpiration,
		@ConfigProperty(name = "mp.jwt.expiration.extended")
			long extendedExpiration,
		@ConfigProperty(name = "mp.jwt.verify.issuer")
			String issuer
	) throws Exception {
		this.defaultExpiration = defaultExpiration;
		this.extendedExpiration = extendedExpiration;
		this.sigKeyId = privateKeyLocation;
		this.issuer = issuer;
		
		this.privateKey = readPrivateKey(privateKeyLocation); //KeyUtils.readPrivateKey(privateKeyLocation); //     StaticUtils.resourceAsUrl(privateKeyLocation).toString());
	}
	
	public String generateTokenString(
		User user,
		boolean extendedTimeout
	) {
		JwtClaimsBuilder claims = Jwt.claims(this.getUserClaims(user));
		
		long currentTimeInSecs = StaticUtils.currentTimeInSecs();
		long expirationTime = StaticUtils.currentTimeInSecs() + (extendedTimeout
																 ? this.extendedExpiration
																 : this.defaultExpiration);
		
		claims.expiresAt(expirationTime);
		claims.claim(Claims.auth_time.name(), currentTimeInSecs);
		
		return claims.jws().signatureKeyId(this.sigKeyId).sign(this.privateKey);
	}
	
	private Map<String, Object> getUserClaims(User user) {
		Map<String, Object> output = this.getBaseClaims();
		
		String userIdentification = user.id + ";" + user.getUsername() + ";" + user.getEmail();
		
		output.put(
			"jti",
			user.id + "-" + user.getLastLogin().getTime() + "-" + user.getNumLogins()
		);//TODO: move to utility, test
		output.put("sub", userIdentification);
		output.put("aud", userIdentification);
		output.put("upn", user.getEmail());
		output.put(JWT_USER_ID_CLAIM, user.id);
		
		output.put("roleMappings", new HashMap<String, Object>());
		
		List<Object> groups = new ArrayList<>();
		
		groups.add(UserLevel.REGULAR.name());
		if(user.getLevel() == UserLevel.ADMIN) {
			groups.add(UserLevel.ADMIN.name());
		}
		
		output.put("groups", groups);
		
		return output;
	}
	
	private Map<String, Object> getBaseClaims() {
		Map<String, Object> output = new HashMap<>();
		
		output.put("iss", this.issuer); // serverInfo.getOrganization() + " - Task Timekeeper Server");
		
		return output;
	}
	
	/**
	 * Read a PEM encoded private key from the classpath
	 *
	 * @param pemResName - key file resource name
	 * @return PrivateKey
	 * @throws Exception on decode failure
	 */
	private static PrivateKey readPrivateKey(final String pemResName) throws Exception {
		log.info("Reading in private key.");
		
		URL url = StaticUtils.resourceAsUrl(pemResName);
		log.debug("Private key location: {}", url);
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
	public static PrivateKey decodePrivateKey(final String pemEncoded)
		throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedBytes = toEncodedBytes(pemEncoded);
		log.info("Decoding private key.");
		
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(keySpec);
		} catch(NoSuchAlgorithmException e) {
			log.error("Algorithm not accepted. this should not happen.");
			throw e;
		} catch(InvalidKeySpecException e) {
			log.error("Could not decode private key: ", e);
			throw e;
		}
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
