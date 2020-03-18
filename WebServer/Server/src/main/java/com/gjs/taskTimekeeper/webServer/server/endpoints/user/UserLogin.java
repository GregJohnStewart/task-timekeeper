package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserLoginRequest;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserLoginResponse;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/user/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserLogin {

    private final PasswordService passwordService;

    private long numLoggedIn = 0;

    public UserLogin(PasswordService passwordService) {
        this.passwordService = passwordService;
    }


    /**
     * TODO:: test
     * @param loginRequest
     * @return
     */
    @POST
    @Counted(name = "numRequests", description = "How many user login requests handled.")
    @Timed(name = "requestTimer", description = "A measure of how long it takes to validate user credentials and make a token to return.", unit = MetricUnits.MILLISECONDS)
    @Operation(
            summary = "Logs a user in, returning a token to interact with the api."
    )
    @APIResponse(
            responseCode = "202",
            description = "User was created.",
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
    public Response loginUser(UserLoginRequest loginRequest){
        User user = User.findByEmailOrUsername(loginRequest.getUser());

        this.passwordService.assertPasswordMatchesHash(user.getHashedPass(), loginRequest.getPlainPass());

        this.numLoggedIn++;
        return Response.status(Response.Status.ACCEPTED).entity(new UserLoginResponse(
                "<token>"
        )).build();
    }
}
