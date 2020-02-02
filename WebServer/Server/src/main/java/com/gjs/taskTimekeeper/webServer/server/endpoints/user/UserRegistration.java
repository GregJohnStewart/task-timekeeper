package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.NotificationSettings;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.pojo.UserLevel;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserRegistrationRequest;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserRegistrationResponse;
import io.quarkus.mailer.MailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.ZonedDateTime;

@Path("/user/registration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRegistration {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistration.class);

    private final PasswordService passwordService;
    private MailTemplate welcomeEmailTemplate;

    public UserRegistration(
            PasswordService passwordService
//            @ResourcePath("email/welcomeVerification")
//            MailTemplate welcomeEmailTemplate
    ){
        this.passwordService = passwordService;
//        this.welcomeEmailTemplate = welcomeEmailTemplate;
    }

    @POST
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        LOGGER.info("Got User Registration request.");

        User newUser = new User();

        newUser.setUsername(request.getUsername());//TODO:: validate
        newUser.setEmail(request.getEmail());//TODO:: validate
        newUser.setEmailValidated(false);
        newUser.setHashedPass(
                passwordService.createPasswordHash(request.getPlainPassword())
        );

        newUser.setJoinDateTime(ZonedDateTime.now());


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

        //TODO:: send validation email

        newUser.persist();

        return new UserRegistrationResponse(
                newUser.getUsername(),
                newUser.getEmail(),
                newUser.id.toHexString()
        );
    }
}
