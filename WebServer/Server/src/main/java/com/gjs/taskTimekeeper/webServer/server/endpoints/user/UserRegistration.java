package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.NotificationSettings;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.UserLevel;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserRegistrationRequest;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserRegistrationResponse;
import com.gjs.taskTimekeeper.webServer.server.validation.EmailValidator;
import com.gjs.taskTimekeeper.webServer.server.validation.UsernameValidator;
import io.quarkus.mailer.MailTemplate;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/api/user/registration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRegistration {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistration.class);

    private final PasswordService passwordService;
    private final UsernameValidator usernameValidator;
    private final EmailValidator emailValidator;
    private MailTemplate welcomeEmailTemplate;

    //stats
    private long numAdded = 0;

    public UserRegistration(
            PasswordService passwordService,
            UsernameValidator usernameValidator,
            EmailValidator emailValidator
//            @ResourcePath("email/welcomeVerification")
//            MailTemplate welcomeEmailTemplate
    ){
        this.passwordService = passwordService;
        this.usernameValidator = usernameValidator;
        this.emailValidator = emailValidator;
//        this.welcomeEmailTemplate = welcomeEmailTemplate;
    }

    @POST
    @Counted(name = "numRequests", description = "How many user registration requests handled.")
    @Timed(name = "requestTimer", description = "A measure of how long it takes to validate and add the user.", unit = MetricUnits.MILLISECONDS)
    @Operation(
            summary = "Adds a user to the database of users."
    )
    @APIResponse(
            responseCode = "201",
            description = "User was created.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserRegistrationResponse.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Bad request given. Data given could not pass validation. (duplicate email/ username, bad password, etc.)",
            content = @Content(mediaType = "text/plain")
    )
    @Tags({@Tag(name="User")})
    public Response registerUser(UserRegistrationRequest request) {
        LOGGER.info("Got User Registration request.");

        User newUser = new User();

        newUser.setUsername(
                this.usernameValidator.validateSanitizeAssertDoesntExist(request.getUsername())
        );
        newUser.setEmail(
                this.emailValidator.validateSanitizeAssertDoesntExist(request.getEmail())
        );
        newUser.setHashedPass(
                this.passwordService.createPasswordHash(request.getPlainPassword())
        );
        LOGGER.debug("Finished validating, valid user registration request.");

        newUser.setEmailValidated(false);

        newUser.setJoinDateTime(
                Date.from(java.time.ZonedDateTime.now().toInstant())
        );


        if(User.listAll().size() < 1) {
            LOGGER.info("First user to register. Making them an admin.");
            newUser.setLevel(UserLevel.ADMIN);
            newUser.setApprovedUser(true);
        } else {
            LOGGER.info("Creating a regular user.");
            newUser.setLevel(UserLevel.REGULAR);
            newUser.setApprovedUser(false);
        }
        newUser.setNotificationSettings(new NotificationSettings(true));

        //TODO:: enable when working
//        CompletionStage<Void> completionStage = this.welcomeEmailTemplate.to(newUser.getEmail())
//                .subject("Welcome to the TaskTimekeeper Server")
//                .send();

        newUser.persist();
        this.numAdded++;
        return Response.status(Response.Status.CREATED).entity(new UserRegistrationResponse(
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.id.toHexString()
        )).build();
    }

    @Gauge(name = "numAdded", unit = MetricUnits.NONE, description = "The number of users actually added.")
    public Long highestPrimeNumberSoFar() {
        return this.numAdded;
    }
}
