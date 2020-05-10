package com.gjs.taskTimekeeper.webServer.server.endpoints.timeManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gjs.taskTimekeeper.baseCode.core.objects.TimeManager;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.webServer.server.exception.WebServerException;
import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.ManagerEntity;
import com.gjs.taskTimekeeper.webServer.webLibrary.timeManager.WholeTimeManagerResponse;
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


/**
 * TODO:: finish this
 */
@Path("/api/timeManager/manager")
@RequestScoped
public class WholeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WholeManager.class);

    private static final ObjectMapper MANAGER_MAPPER = ObjectMapperUtilities.getTimeManagerObjectMapper();

    @Inject
    JsonWebToken jwt;

    @GET
    @Counted(name = "numRequests", description = "How many manager requests handled.")
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
            description = "User was not authorized to get the info on another user",
            content = @Content(mediaType = "text/plain")
    )
    @Tags({@Tag(name="Time Manager")})
    @SecurityRequirement(name="JwtAuth")
    @RolesAllowed({"ADMIN", "REGULAR"})
    @Path("/{userId}")
    public Response getTimeManager(@Context SecurityContext ctx){
        ObjectId userId = jwt.getClaim("userId");
        LOGGER.info("Getting Time Manager data for user {}", userId);

        ManagerEntity entity;

        try{
            entity = ManagerEntity.findByUserId(userId);
        } catch (EntityNotFoundException e){
            try {
                entity = new ManagerEntity(
                        userId,
                        MANAGER_MAPPER.writeValueAsBytes(new TimeManager()),
                        null
                );
            } catch (JsonProcessingException e2) {
                LOGGER.error("Failed to create new empty manager entity for user: ", e2);
                throw new WebServerException("Failed to create new empty manager entity for user: ", e2);
            }
            entity.persist();
        }

        return Response
                .status(200)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(
                        new WholeTimeManagerResponse(
                                entity.getTimeManagerData(),
                                entity.getLastUpdate()
                        )
                )
                .build();
    }

    //TODO: PATCH or POST to update
}
