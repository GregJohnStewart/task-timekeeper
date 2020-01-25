package com.gjs.taskTimekeeper.webServer.server.endpoints.user;

import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserRegistrationRequest;
import com.gjs.taskTimekeeper.webServer.server.toMoveToLib.UserRegistrationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user/registration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserRegistration {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegistration.class);



    @POST
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        LOGGER.info("Got User Registration request.");




        return new UserRegistrationResponse(request.getUsername(), request.getEmail(), "new id");
    }
}
