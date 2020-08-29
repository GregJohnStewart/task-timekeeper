package com.gjs.taskTimekeeper.webServer.server.endpoints.timeManager;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.utils.LoggingUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.TimeManagerLastUpdateCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.spi.HttpRequest;

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
import java.io.IOException;

@Path("/api/timeManager/lastUpdate")
@RequestScoped
@Slf4j
public class LastUpdate {
	@Inject
	JsonWebToken jwt;
	
	@GET
	@Counted(name = "numGetRequests", description = "How many manager data get requests handled.")
	@Operation(
		summary = "Gets the user's time manager data."
	)
	@APIResponse(
		responseCode = "200",
		description = "Got the user's time manager data.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = TimeManagerLastUpdateCheckResponse.class)
		)
	)
	@APIResponse(
		responseCode = "204",
		description = "User's manager entity was not found, did they create one yet?",
		content = @Content(mediaType = "text/plain")
	)
	@APIResponse(
		responseCode = "401",
		description = "Something went wrong authorizing the user.",
		content = @Content(mediaType = "text/plain")
	)
	@Tags({@Tag(name = "Time Manager")})
	@SecurityRequirement(name = "JwtAuth")
	@RolesAllowed({"ADMIN", "REGULAR"})
	@Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	public Response getTimeManager(
		@Context
			SecurityContext ctx,
		@Context
			HttpRequest context
	) throws IOException {
		ObjectId userId = new ObjectId((String)jwt.getClaim("userId"));
		LoggingUtils.endpointInfoLog(
			log,
			context,
			"Getting Time Manager data for user {}",
			userId
		);
		
		ManagerEntity managerEntity;
		
		try {
			managerEntity = ManagerEntity.findByUserId(userId);
		} catch(EntityNotFoundException e) {
			return Response.noContent().type(MediaType.TEXT_PLAIN).entity("User has not set manager data yet.").build();
		}
		
		return Response
			.status(Response.Status.OK.getStatusCode())
			.type(MediaType.APPLICATION_JSON)
			.entity(new TimeManagerLastUpdateCheckResponse(managerEntity.getLastUpdate()))
			.build();
	}
}
