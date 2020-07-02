package com.gjs.taskTimekeeper.webServer.server.endpoints.timeManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;
import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.WholeTimeManagerResponse;
import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.WholeTimeManagerUpdateRequest;
import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.WholeTimeManagerUpdateResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * TODO:: test
 */
@Path("/api/timeManager/manager")
@RequestScoped
public class WholeManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(WholeManager.class);
	
	private static final ObjectMapper MANAGER_MAPPER = ObjectMapperUtilities.getTimeManagerObjectMapper();
	
	@Inject
	JsonWebToken jwt;
	
	/**
	 * Gets the user's manager entity. In the case where they don't already have one, an empty time manager is created for them.
	 *
	 * @param userId The id of the user to get the managerEntity for
	 * @return
	 */
	private ManagerEntity getOrCreateNew(ObjectId userId) {
		ManagerEntity entity;
		
		try {
			entity = ManagerEntity.findByUserId(userId);
		} catch(EntityNotFoundException e) {
			try {
				entity = new ManagerEntity(
					userId,
					MANAGER_MAPPER.writeValueAsBytes(new TimeManager()),
					null
				);
			} catch(JsonProcessingException e2) {
				LOGGER.error("Failed to create new empty manager entity for user: ", e2);
				throw new WebServerException("Failed to create new empty manager entity for user: ", e2);
			}
			entity.persist();
		}
		return entity;
	}
	
	private WholeTimeManagerUpdateResponse toUpdateResponse(ManagerEntity entity, boolean changed) {
		try {
			return new WholeTimeManagerUpdateResponse(
				MANAGER_MAPPER.readValue(entity.getTimeManagerData(), TimeManager.class),
				entity.getLastUpdate(),
				changed
			);
		} catch(IOException e) {
			LOGGER.error("FAILED to serialize stored time manager.");
			throw new RuntimeException(e);
		}
	}
	
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
			schema = @Schema(implementation = WholeTimeManagerResponse.class)
		)
	)
	@APIResponse(
		responseCode = "401",
		description = "Something went wrong authorizing the user.",
		content = @Content(mediaType = "text/plain")
	)
	@Tags({@Tag(name = "Time Manager")})
	@SecurityRequirement(name = "JwtAuth")
	@RolesAllowed({"ADMIN", "REGULAR"})
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimeManager(
		@Context
			SecurityContext ctx
	) throws IOException {
		ObjectId userId = new ObjectId((String)jwt.getClaim("userId"));
		LOGGER.info("Getting Time Manager data for user {}", userId);
		
		ManagerEntity entity = getOrCreateNew(userId);
		
		return Response
			.status(Response.Status.OK.getStatusCode())
			.type(MediaType.APPLICATION_JSON_TYPE)
			.entity(
				new WholeTimeManagerResponse(
					MANAGER_MAPPER.readValue(entity.getTimeManagerData(), TimeManager.class),
					entity.getLastUpdate()
				)
			)
			.build();
	}
	
	@PATCH
	@Counted(name = "numPatchRequests", description = "How many manager data update requests handled.")
	@Operation(
		summary = "Updates the user's time manager data."
	)
	@APIResponse(
		responseCode = "201",
		description = "Updated the user's time manager data.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = WholeTimeManagerUpdateResponse.class)
		)
	)
	@APIResponse(
		responseCode = "304",
		description = "The data given was the same as the data held.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = WholeTimeManagerUpdateResponse.class)
		)
	)
	@APIResponse(
		responseCode = "400",
		description = "The time manager data given was invalid. Returns the data held.",
		content = @Content(mediaType = "application/json")
	)
	@APIResponse(
		responseCode = "401",
		description = "Something went wrong authorizing the user.",
		content = @Content(mediaType = "text/plain")
	)
	@Tags({@Tag(name = "Time Manager")})
	@SecurityRequirement(name = "JwtAuth")
	@RolesAllowed({"ADMIN", "REGULAR"})
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response patchTimeManager(
		WholeTimeManagerUpdateRequest updateRequest,
		@Context
			SecurityContext ctx
	)
		throws JsonProcessingException {
		ObjectId userId = jwt.getClaim("userId");
		LOGGER.info("Updating Time Manager data for user {}", userId);
		
		ManagerEntity entity = getOrCreateNew(userId);
		byte[] managerData = null;
		
		try {
			managerData = MANAGER_MAPPER.writeValueAsBytes(updateRequest.getTimeManagerData());
		} catch(JsonProcessingException e) {
			LOGGER.warn("FAILED to serialize manager object given by user.");
			throw e;
		}
		
		if(
			Arrays.equals(
				entity.getTimeManagerData(),
				managerData
			)
		) {
			LOGGER.info("User posted the same data that was already held.");
			return Response
				.status(Response.Status.NOT_MODIFIED.getStatusCode())
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(toUpdateResponse(entity, true))
				.build();
		}
		
		entity.setTimeManagerData(managerData);
		entity.setLastUpdate(new Date());
		entity.update();
		
		return Response
			.status(Response.Status.ACCEPTED.getStatusCode())
			.type(MediaType.APPLICATION_JSON_TYPE)
			.entity(toUpdateResponse(entity, true))
			.build();
	}
}
