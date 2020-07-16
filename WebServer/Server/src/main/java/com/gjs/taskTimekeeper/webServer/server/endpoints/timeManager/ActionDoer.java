package com.gjs.taskTimekeeper.webServer.server.endpoints.timeManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.Action;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.actionDoer.CrudOperator;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import com.gjs.taskTimekeeper.baseCode.stats.processor.AllStatsProcessor;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.server.validation.sanitize.TimeManagerActionDeSanitizer;
import com.gjs.taskTimekeeper.webServer.server.validation.sanitize.TimemanagerResponseSanitizer;
import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.action.TimeManagerActionRequest;
import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.action.TimeManagerActionResponse;
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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

@Path("/api/timeManager/manager/action")
@RequestScoped
public class ActionDoer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDoer.class);
	private static final ObjectMapper MANAGER_MAPPER = ObjectMapperUtilities.getTimeManagerObjectMapper();
	
	@Inject
	JsonWebToken jwt;
	
	@Inject
	TimeManagerActionDeSanitizer actionDeSanitizer;
	
	@Inject
	TimemanagerResponseSanitizer responseSanitizer;
	
	@PATCH
	@Counted(name = "numPatchRequests", description = "How many manager data update requests handled.")
	@Operation(
		summary = "Updates the user's time manager data using a configured action."
	)
	@APIResponse(
		responseCode = "201",
		description = "Updated the user's time manager data.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = TimeManagerActionResponse.class)
		)
	)
	@APIResponse(
		responseCode = "200",
		description = "Nothing was changed.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = TimeManagerActionResponse.class)
		)
	)
	@APIResponse(
		responseCode = "400",
		description = "The time manager action was invalid, or otherwise caused an error. Returns the data held.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = TimeManagerActionResponse.class)
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
		TimeManagerActionRequest updateRequest,
		@Context
			SecurityContext ctx,
		@HeaderParam("provideStats")
			boolean provideStats,
		@HeaderParam("sanitizeText")
			boolean sanitizeText
	) throws IOException {
		ObjectId userId = new ObjectId((String)jwt.getClaim("userId"));
		LOGGER.info(
			"Updating Time Manager data with action for user {}, providing stats? {}. Sanitizing text? {}",
			userId,
			provideStats,
			sanitizeText
		);
		LOGGER.debug("Update request data: {}", updateRequest);
		
		updateRequest = actionDeSanitizer.deSanitize(updateRequest);
		
		ManagerEntity heldEntity = ManagerEntity.getOrCreateNew(userId);
		Date updatedDate = heldEntity.getLastUpdate();
		TimeManager heldManager = MANAGER_MAPPER.readValue(heldEntity.getTimeManagerData(), TimeManager.class);
		ByteArrayOutputStream regStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errStream = new ByteArrayOutputStream();
		
		CrudOperator operator = new CrudOperator(heldManager, new Outputter(regStream, errStream));
		
		if(updateRequest.getSelectedPeriod() != null) {
			LOGGER.info("Selecting user's period {}", updateRequest.getSelectedPeriod());
			ActionConfig selectConfig = new ActionConfig(KeeperObjectType.PERIOD, Action.VIEW);
			selectConfig.setSelect(true);
			selectConfig.setIndex(updateRequest.getSelectedPeriod());
			
			operator.doObjAction(selectConfig);
			
			String errOutput = errStream.toString();
			
			if(!errOutput.isEmpty()) {
				LOGGER.warn(
					"Error Selecting the user's selected period {} - {}",
					updateRequest.getSelectedPeriod(),
					errOutput
				);
				//TODO:: handle here
			}
		}
		
		boolean changed = operator.doObjAction(updateRequest.getActionConfig());
		String regOutput = regStream.toString();
		String errOutput = errStream.toString();
		
		Response.Status status = Response.Status.OK;
		if(changed) {
			updatedDate = heldEntity.updateManagerData(heldManager);
			status = Response.Status.ACCEPTED;
		}
		if(!errOutput.isEmpty()) {
			status = Response.Status.BAD_REQUEST;
		}
		
		TimeManagerActionResponse response = new TimeManagerActionResponse(
			heldManager,
			null,
			updatedDate,
			changed,
			regOutput,
			errOutput
		);
		
		if(provideStats) {
			response.setStats(
				new AllStatsProcessor().process(response.getTimeManagerData())
			);
		}
		
		if(sanitizeText) {
			response = (TimeManagerActionResponse)responseSanitizer.sanitize(response);
		}
		
		return Response
			.status(status)
			.type(MediaType.APPLICATION_JSON_TYPE)
			.entity(response)
			.build();
	}
	
	private Response createActionResponse() {
		return null;
	}
}
