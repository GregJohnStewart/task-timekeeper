package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.UserRequestException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.UserUnauthorizedException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.UserLevel;
import com.gjs.taskTimekeeper.webServer.server.service.JwtService;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Endpoints to allow users' to get info about themselves and others.
 *
 * TODO:: log and test
 */
@Path("/api/user/info/")
@RequestScoped
public class UserInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfo.class);

    @Inject
    JsonWebToken jwt;

    @GET
    @Counted(name = "numGetOneRequests", description = "How many user info requests handled.")
    @Operation(
            summary = "Gets a set of the user's info."
    )
    @APIResponse(
            responseCode = "200",
            description = "Got the user's info.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserInfo.class)
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
    @Tags({@Tag(name="User")})
    @SecurityRequirement(name="JwtAuth")
    @RolesAllowed({"ADMIN", "REGULAR"})
    @Path("/{userId}")
    public Response getUserInfo(@PathParam("userId") String userId, @Context SecurityContext ctx){
        ObjectId userObjectId = new ObjectId(userId);
        LOGGER.info("Got {} as a path parameter.", userId);
        ObjectId jwtObjectId = jwt.getClaim(JwtService.JWT_USER_ID_CLAIM);
        ObjectId userIdToGetInfoOf = userObjectId;
        if(userIdToGetInfoOf == null){
            userIdToGetInfoOf = jwtObjectId;
        }
        if(userIdToGetInfoOf == null){
            throw new UserRequestException("Could not get a user id to find.");
        }
        if(!jwtObjectId.equals(userIdToGetInfoOf) && !ctx.isUserInRole(UserLevel.ADMIN.name())){
            throw new UserUnauthorizedException("Not authorized to view the info about another user.");
        }

        User user = User.findById(userIdToGetInfoOf);
        if(user == null){
            throw new EntityNotFoundException("User requested could not be found.");
        }
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(user.toUserInfo()).build();
    }

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
                            implementation = com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserInfo.class
                    )
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Bad request given. Data given could not pass validation. (no user at given id, etc.)",
            content = @Content(mediaType = "text/plain")
    )
    @Tags({@Tag(name="User")})
    @SecurityRequirement(name="JwtAuth")
    @RolesAllowed({"ADMIN"})
    public Response getUsersInfo(@Context SecurityContext ctx){
        List<User> users = User.listAll();
        List<com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserInfo> userInfos = new ArrayList<>(users.size());

        for(User curUser : users){
            userInfos.add(curUser.toUserInfo());
        }

        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(userInfos).build();
    }

}
