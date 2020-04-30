package com.gjs.taskTimekeeper.webServer.server.endpoints.user;


import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/api/user/emailValidation")
public class UserEmailValidation {

    private final PasswordService passwordService;

    public UserEmailValidation(PasswordService passwordService) {
        this.passwordService = passwordService;
    }


    //TODO:: make return a html page to return to main ui
    @GET
    @Counted(name = "numRequests", description = "How many user email validation requests handled.")
    @Timed(name = "requestTimer", description = "A measure of how long it takes to validate user's email.", unit = MetricUnits.MILLISECONDS)
    @Operation(
            summary = "Validates the code in the link sent to the user's email."
    )
    @APIResponse(
            responseCode = "200",
            description = "User's email was validated.",
            content = @Content(
                    mediaType = "text/plain"
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Bad request given. Data given could not pass validation. (wrong key given for user, etc.)",
            content = @Content(mediaType = "text/plain")
    )
    @Tags({@Tag(name="User")})
    public Response validateUserEmail(@QueryParam("validationToken") String validationToken, @QueryParam("userId") ObjectId userId) {
        User user = User.findById(userId);

        if(user == null){
            throw new EntityNotFoundException("User was not found.");
        }


        this.passwordService.assertPasswordMatchesHash(user.getEmailValidationToken(), validationToken);

        user.setEmailValidated(true);
        user.setEmailValidationToken(null);
        user.setLastEmailValidated(new Date());

        user.update();

        return Response.status(200).type(MediaType.TEXT_PLAIN_TYPE).build();
    }

    //TODO:: endpoint to resend

}
