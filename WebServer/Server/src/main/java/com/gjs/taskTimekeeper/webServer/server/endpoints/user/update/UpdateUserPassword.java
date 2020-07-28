package com.gjs.taskTimekeeper.webServer.server.endpoints.user.update;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.JwtService;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.service.UserNotificationService;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.update.UserUpdatePasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

@Path("/api/user/update/password")
@Slf4j
public class UpdateUserPassword {
	
	private final UserNotificationService notificationService;
	private final PasswordService passwordService;
	
	@Inject
	JsonWebToken jwt;
	
	public UpdateUserPassword(
		UserNotificationService notificationService,
		PasswordService passwordService
	) {
		this.notificationService = notificationService;
		this.passwordService = passwordService;
	}
	
	@PATCH
	@Counted(name = "numRequests", description = "How many user password change requests handled.")
	@Timed(name = "requestTimer",
		   description = "A measure of how long it takes to update the user's password.",
		   unit = MetricUnits.MILLISECONDS)
	@Operation(
		summary = "Updates a user's password."
	)
	@APIResponse(
		responseCode = "204",
		description = "User's password was updated."
	)
	@APIResponse(
		responseCode = "400",
		description = "Bad request given. Data given could not pass validation. (bad new/old password)",
		content = @Content(mediaType = "text/plain")
	)
	@APIResponse(
		responseCode = "401",
		description = "Bad authorization given.",
		content = @Content(mediaType = "text/plain")
	)
	@Tags({@Tag(name = "User")})
	@SecurityRequirement(name = "JwtAuth")
	@RolesAllowed({"REGULAR", "ADMIN"})
	@Consumes(MediaType.APPLICATION_JSON)
	public CompletionStage<Object> registerUser(UserUpdatePasswordRequest request) {
		ObjectId userId = new ObjectId((String)jwt.getClaim(JwtService.JWT_USER_ID_CLAIM));
		User user = User.findById(userId);
		log.debug("Attempting up update user password: {}", userId);
		
		this.passwordService.assertPasswordMatchesHash(user.getHashedPass(), request.getOldPlainPassword());
		
		log.debug("User's old passwords matched.");
		CompletionStage<Void> completionStage = this.notificationService.alertToAccountChange(
			user,
			"password changed",
			"Your password was changed."
		);
		
		user.setHashedPass(this.passwordService.createPasswordHash(request.getNewPlainPassword()));
		user.update();
		
		log.debug("User's passwords were updated.");
		
		return completionStage.thenApply(
			x->Response.noContent().build()
		);
	}
}
