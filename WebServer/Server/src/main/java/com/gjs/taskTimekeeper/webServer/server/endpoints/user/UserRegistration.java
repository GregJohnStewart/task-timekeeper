package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.NotificationSettings;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.service.TokenService;
import com.gjs.taskTimekeeper.webServer.server.validation.EmailValidator;
import com.gjs.taskTimekeeper.webServer.server.validation.UsernameValidator;
import com.gjs.taskTimekeeper.webServer.webLibrary.UserLevel;
import com.gjs.taskTimekeeper.webServer.webLibrary.UserRegistrationRequest;
import com.gjs.taskTimekeeper.webServer.webLibrary.UserRegistrationResponse;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.RawString;
import io.quarkus.qute.api.ResourcePath;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.CompletionStage;

@Path("/api/user/registration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRegistration {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistration.class);

    private final PasswordService passwordService;
    private final UsernameValidator usernameValidator;
    private final EmailValidator emailValidator;
    private final TokenService tokenService;
    private final boolean newUserAutoApprove;
    private final MailTemplate welcomeEmailTemplate;
    private final ServerInfoBean serverInfoBean;

    //stats
    private long numAdded = 0;

    public UserRegistration(
            PasswordService passwordService,
            UsernameValidator usernameValidator,
            EmailValidator emailValidator,
            @ConfigProperty(name="user.new.autoApprove")
            boolean newUserAutoApprove,
            @ResourcePath("email/welcomeVerification")
            MailTemplate welcomeEmailTemplate,
            TokenService tokenService,
            ServerInfoBean serverInfoBean
    ){
        this.passwordService = passwordService;
        this.usernameValidator = usernameValidator;
        this.emailValidator = emailValidator;
        this.newUserAutoApprove = newUserAutoApprove;
        this.welcomeEmailTemplate = welcomeEmailTemplate;
        this.tokenService = tokenService;
        this.serverInfoBean = serverInfoBean;
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
    public CompletionStage<Response> registerUser(UserRegistrationRequest request) throws UnsupportedEncodingException, MalformedURLException {
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

        if(User.listAll().size() < 1) {
            LOGGER.info("First user to register. Making them an admin.");
            newUser.setLevel(UserLevel.ADMIN);
            newUser.setApprovedUser(true);
        } else {
            LOGGER.info("Creating a regular user.");
            newUser.setLevel(UserLevel.REGULAR);
            newUser.setApprovedUser(this.newUserAutoApprove);
        }
        newUser.setNotificationSettings(new NotificationSettings(true));

        String emailValidationToken = this.tokenService.generateToken();
        newUser.setEmailValidationToken(
                this.passwordService.createPasswordHash(emailValidationToken)
        );

        newUser.persist();

        //TODO:: http/s selection
        String validationLink = "http://" +
                        this.serverInfoBean.getHostname() +
                        ":" +
                        this.serverInfoBean.getPort() +
                        "/api/user/emailValidation" +
                        "?" +
                        "userId=" + newUser.id +
                        "&" +
                        "validationToken=" + emailValidationToken;

        CompletionStage<Void> completionStage = this.welcomeEmailTemplate
                .to(newUser.getEmail())
                .subject("Welcome to the TaskTimekeeper Server")
                .data("name", newUser.getUsername())
                .data("validationLink", new RawString(validationLink))
                .send();


        this.numAdded++;
        return completionStage.thenApply(
                x -> Response.status(Response.Status.CREATED).entity(new UserRegistrationResponse(
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.id.toHexString()
        )).build()
        );
    }

    @Gauge(name = "numAdded", unit = MetricUnits.NONE, description = "The number of users actually added.")
    public Long highestPrimeNumberSoFar() {
        return this.numAdded;
    }
}
