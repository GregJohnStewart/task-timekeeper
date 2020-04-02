package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.service.JwtService;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
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


@Path("/api/user/info/")
@RequestScoped
public class UserInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfo.class);

    @Inject
    JsonWebToken jwt;

    @GET
    @Counted(name = "numRequests", description = "How many user info requests handled.")
    @Operation(
            summary = "Gets a set of the user's info."
    )
    @APIResponse(
            responseCode = "200",
            description = "Got the user's info.",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Bad request given. Data given could not pass validation. (no user at given id, etc.)",
            content = @Content(mediaType = "text/plain")
    )
    @Tags({@Tag(name="User")})
    @SecurityRequirement(name="JwtAuth")
    @RolesAllowed({"ADMIN", "REGULAR"})
    @Path("/{userId}")
    public Response getUserInfo(@PathParam("userId") ObjectId userId, @Context SecurityContext ctx){
        LOGGER.info("Got {} as a path parameter.", userId);
        ObjectId userIdToGetInfoOf = userId;
        if(userIdToGetInfoOf == null){
            jwt.getClaim(JwtService.JWT_USER_ID_CLAIM);
        }

        UserInfo info = new UserInfo();


        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(info).build();
    }

}
