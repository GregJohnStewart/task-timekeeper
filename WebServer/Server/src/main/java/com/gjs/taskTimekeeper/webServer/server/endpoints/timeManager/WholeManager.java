package com.gjs.taskTimekeeper.webServer.server.endpoints.timeManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.stats.processor.AllStatsProcessor;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.validation.sanitize.TimemanagerAnitizer;
import com.gjs.taskTimekeeper.webServer.server.validation.sanitize.TimemanagerResponseAnitizer;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.TimeManagerResponse;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.whole.WholeTimeManagerUpdateRequest;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.whole.WholeTimeManagerUpdateResponse;
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

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

import static com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity.MANAGER_MAPPER;
import static com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity.getOrCreateNew;

/**
 * TODO:: test thoroughly
 */
@Path("/api/timeManager/manager")
@RequestScoped
@Slf4j
public class WholeManager {
	
	@Inject
	JsonWebToken jwt;
	
	@Inject
	TimemanagerResponseAnitizer responseSanitizer;
	
	@Inject
	TimemanagerAnitizer timemanagerAnitizer;
	
	private WholeTimeManagerUpdateResponse toUpdateResponse(
		ManagerEntity entity,
		boolean changed,
		boolean provideStats,
		boolean sanitizeText
	) {
		WholeTimeManagerUpdateResponse response = new WholeTimeManagerUpdateResponse();
		try {
			response.setTimeManagerData(MANAGER_MAPPER.readValue(entity.getTimeManagerData(), TimeManager.class));
		} catch(IOException e) {
			log.error("FAILED to serialize stored time manager.");
			throw new RuntimeException(e);
		}
		
		response.setLastUpdated(entity.getLastUpdate());
		response.setChanged(changed);
		
		if(provideStats) {
			response.setStats(
				new AllStatsProcessor().process(response.getTimeManagerData())
			);
		}
		
		if(sanitizeText) {
			response = (WholeTimeManagerUpdateResponse)responseSanitizer.sanitize(response);
		}
		
		return response;
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
			schema = @Schema(implementation = TimeManagerResponse.class)
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
			SecurityContext ctx,
		@HeaderParam("provideStats")
			boolean provideStats,
		@HeaderParam("sanitizeText")
			boolean sanitizeText
	) throws IOException {
		ObjectId userId = new ObjectId((String)jwt.getClaim("userId"));
		log.info("Getting Time Manager data for user {}", userId);
		
		ManagerEntity entity = getOrCreateNew(userId);
		
		TimeManagerResponse output = new TimeManagerResponse(
			MANAGER_MAPPER.readValue(entity.getTimeManagerData(), TimeManager.class),
			null,
			entity.getLastUpdate()
		);
		
		if(provideStats) {
			output.setStats(
				new AllStatsProcessor().process(output.getTimeManagerData())
			);
		}
		
		if(sanitizeText) {
			output = responseSanitizer.sanitize(output);
		}
		return Response
			.status(Response.Status.OK.getStatusCode())
			.type(MediaType.APPLICATION_JSON_TYPE)
			.entity(output)
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
		responseCode = "200",
		description = "The data given was the same as the data held.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = WholeTimeManagerUpdateResponse.class)
		)
	)
	@APIResponse(
		responseCode = "400",
		description = "The time manager data given was invalid. Returns the data held.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = WholeTimeManagerUpdateResponse.class)
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
	@Consumes(MediaType.APPLICATION_JSON)
	public Response patchTimeManager(
		WholeTimeManagerUpdateRequest updateRequest,
		@Context
			SecurityContext ctx,
		@HeaderParam("provideStats")
			boolean provideStats,
		@HeaderParam("sanitizeText")
			boolean sanitizeText
	)
		throws JsonProcessingException {
		ObjectId userId = new ObjectId((String)jwt.getClaim("userId"));
		log.info(
			"Updating Time Manager data for user {}, providing stats? {}. Sanitizing text? {}",
			userId,
			provideStats,
			sanitizeText
		);
		
		//TODO:: unsanitize text data in request
		
		ManagerEntity entity = getOrCreateNew(userId);
		byte[] managerData = null;
		
		try {
			TimeManager manager = updateRequest.getTimeManagerData();
			manager = timemanagerAnitizer.deSanitize(manager);
			
			managerData = MANAGER_MAPPER.writeValueAsBytes(manager);
		} catch(JsonProcessingException e) {
			log.warn("FAILED to serialize manager object given by user.");
			throw e;
		}
		
		if(
			Arrays.equals(
				entity.getTimeManagerData(),
				managerData
			)
		) {
			log.info("User posted the same data that was already held.");
			return Response
				.status(Response.Status.OK.getStatusCode())
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(toUpdateResponse(entity, true, provideStats, sanitizeText))
				.build();
		}
		
		entity.setTimeManagerData(managerData);
		entity.setLastUpdate(new Date());
		entity.update();
		
		return Response
			.status(Response.Status.ACCEPTED.getStatusCode())
			.type(MediaType.APPLICATION_JSON_TYPE)
			.entity(toUpdateResponse(entity, true, provideStats, sanitizeText))
			.build();
	}
}
