package com.gjs.taskTimekeeper.webServer.server.endpoints.user.auth;

import com.gjs.taskTimekeeper.webServer.server.exception.request.user.TooManyFailedLoginsException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.UserLockedException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.UserLoginException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.JwtService;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.utils.LoggingUtils;
import com.gjs.taskTimekeeper.webServer.server.utils.UserUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.auth.UserLoginRequest;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.auth.UserLoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/api/user/auth/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class UserLogin {
	
	private final PasswordService passwordService;
	private final JwtService jwtService;
	private final UserUtils userUtils;
	
	private long numLoggedIn = 0;
	
	public UserLogin(
		PasswordService passwordService,
		JwtService jwtService,
		UserUtils userUtils
	) {
		this.passwordService = passwordService;
		this.jwtService = jwtService;
		this.userUtils = userUtils;
	}
	
	@POST
	@Counted(name = "numRequests", description = "How many user login requests handled.")
	@Timed(name = "requestTimer",
		   description = "A measure of how long it takes to validate user credentials and make a token to return.",
		   unit = MetricUnits.MILLISECONDS)
	@Operation(
		summary = "Logs a user in, returning a token to interact with the api."
	)
	@APIResponse(
		responseCode = "202",
		description = "User was logged in.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = UserLoginResponse.class)
		)
	)
	@APIResponse(
		responseCode = "401",
		description = "Incorrect credentials given.",
		content = @Content(mediaType = "text/plain")
	)
	@APIResponse(
		responseCode = "403",
		description = "If the account has been locked.",
		content = @Content(mediaType = "text/plain")
	)
	@APIResponse(
		responseCode = "429",
		description = "Happens when too many requests to login were sent in a given time period.",
		content = @Content(mediaType = "text/plain")
	)
	@Tags({@Tag(name = "User"), @Tag(name = "Auth")})
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginUser(
		UserLoginRequest loginRequest,
		@Context
			HttpRequest context
	) {
		User user = User.findByEmailOrUsername(loginRequest.getUser());
		LoggingUtils.endpointInfoLog(
			log,
			context,
			"Attempting to log in user: {}",
			user.id
		);
		
		//TODO:: in user utils, cull login attempts not in last hour, add this one
		
		//TODO:: fix this next line's broken logic
		if(userUtils.canLogin(user)) {
			if(!userUtils.numLoginAttemptsDoesNotExceed(user)) {
				throw new TooManyFailedLoginsException("User account has been locked. Please contact admin.");
			}
			if(user.getLoginAuth().isLocked()) {
				throw new UserLockedException("User account has been locked. Please contact admin.");
			}
			throw new UserLoginException("User account is not able to login. For questions, contact admin.");
		}
		
		this.passwordService.assertPasswordMatchesHash(
			user.getLoginAuth().getHashedPass(),
			loginRequest.getPlainPass()
		);
		
		user.getLoginAuth().setLastLogin(new Date());
		user.getLoginAuth().setNumLogins(user.getLoginAuth().getNumLogins() + 1);
		
		user.update();
		
		String jwt = this.jwtService.generateTokenString(user, loginRequest.isExtendedTimeout());
		
		this.numLoggedIn++;
		return Response.status(Response.Status.ACCEPTED).entity(new UserLoginResponse(
			jwt
		)).build();
	}
	
	@Gauge(name = "numLoggedIn", unit = MetricUnits.NONE, description = "The number of users tht were given jwt keys.")
	public Long highestPrimeNumberSoFar() {
		return this.numLoggedIn;
	}
}
