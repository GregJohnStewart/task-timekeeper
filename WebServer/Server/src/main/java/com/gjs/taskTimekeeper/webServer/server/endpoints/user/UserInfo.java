package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Endpoints to allow users' to get info about themselves and others.
 * <p>
 * TODO:: log and test
 */
@Path("/api/user/info/")
@RequestScoped
@Slf4j
public class UserInfo {
	
	@Inject
	JsonWebToken jwt;
	
	@GET
	@Counted(name = "numGetOwnRequests", description = "How many own user info requests handled.")
	@Operation(
		summary = "Gets the user's info who made the request."
	)
	@APIResponse(
		responseCode = "200",
		description = "Got the user's info.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(
				implementation = com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserInfo.class
			)
		)
	)
	@APIResponse(
		responseCode = "400",
		description = "Bad request given. Data given could not pass validation. (no user at given id, etc.)",
		content = @Content(mediaType = "text/plain")
	)
	@Tags({@Tag(name = "User")})
	@SecurityRequirement(name = "JwtAuth")
	@RolesAllowed({"ADMIN", "REGULAR"})
	@Path("/self")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsersInfo(
		@Context
			SecurityContext ctx
	) {
		ObjectId userId = new ObjectId((String)jwt.getClaim(JwtService.JWT_USER_ID_CLAIM));
		log.debug("Getting user's own info. User: {}", userId);
		User user = User.findById(userId);
		
		return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(
			user.toUserInfo()
		).build();
	}
	
	@GET
	@Counted(name = "numGetOneRequests", description = "How many user info requests handled.")
	@Operation(
		summary = "Gets a set of the given user's info."
	)
	@APIResponse(
		responseCode = "200",
		description = "Got the user's info.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserInfo.class)
		)
	)
	@APIResponse(
		responseCode = "400",
		description = "Bad request given. Data given could not pass validation. (no user at given id, etc.)",
		content = @Content(mediaType = "text/plain")
	)
	@APIResponse(
		responseCode = "401",
		description = "User was not authorized to get the info on another user",
		content = @Content(mediaType = "text/plain")
	)
	@Tags({@Tag(name = "User"), @Tag(name = "Admin Related")})
	@SecurityRequirement(name = "JwtAuth")
	@RolesAllowed({"ADMIN"})
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserInfo(
		@PathParam("userId")
			String userId,
		@Context
			SecurityContext ctx
	) {
		log.info("Got {} as a path parameter.", userId);
		ObjectId userObjectId = new ObjectId(userId);
		
		User user = User.findById(userObjectId);
		if(user == null) {
			throw new EntityNotFoundException("User requested could not be found.");
		}
		return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(user.toUserInfo()).build();
	}
	
	/**
	 * TODO:: filtering params
	 *
	 * @param ctx
	 * @return
	 */
	@GET
	@Counted(name = "numGetAllRequests", description = "How many user info requests handled.")
	@Operation(
		summary = "Gets all the users' info."
	)
	@APIResponse(
		responseCode = "200",
		description = "Got the users' info.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(
				type = SchemaType.ARRAY,
				implementation = com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserInfo.class
			)
		)
	)
	@APIResponse(
		responseCode = "400",
		description = "Bad request given. Data given could not pass validation. (no user at given id, etc.)",
		content = @Content(mediaType = "text/plain")
	)
	@Tags({@Tag(name = "User"), @Tag(name = "Admin Related")})
	@SecurityRequirement(name = "JwtAuth")
	@RolesAllowed({"ADMIN"})
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsersInfo(
		@Context
			SecurityContext ctx
	) {
		List<User> users = User.listAll();
		List<com.gjs.taskTimekeeper.webServer.webLibrary.pojo.user.UserInfo> userInfos = new ArrayList<>(users.size());
		
		for(User curUser : users) {
			userInfos.add(curUser.toUserInfo());
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(userInfos).build();
	}
}
