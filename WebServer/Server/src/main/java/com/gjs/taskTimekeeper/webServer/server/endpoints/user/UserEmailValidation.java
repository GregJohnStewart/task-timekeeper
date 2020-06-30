package com.gjs.taskTimekeeper.webServer.server.endpoints.user;


import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.IncorrectPasswordException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.ValidationException;
import com.gjs.taskTimekeeper.webServer.server.mongoEntities.User;
import com.gjs.taskTimekeeper.webServer.server.service.PasswordService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("/api/user/emailValidation")
@ApplicationScoped
public class UserEmailValidation {
	
	public static final String SUCCESSFULLY_VALIDATED_EMAIL_MESSAGE = "Successfully validated email.";
	private final PasswordService passwordService;
	private final Template redirectResponseTemplate;
	
	public UserEmailValidation(
		PasswordService passwordService,
		@ResourcePath("webPages/redirectWithMessage")
			Template redirectResponseTemplate
	) {
		this.passwordService = passwordService;
		this.redirectResponseTemplate = redirectResponseTemplate;
	}
	
	@GET
	@Counted(name = "numRequests", description = "How many user email validation requests handled.")
	@Timed(name = "requestTimer",
		   description = "A measure of how long it takes to validate user's email.",
		   unit = MetricUnits.MILLISECONDS)
	@Operation(
		summary = "Validates the code in the link sent to the user's email. Returns an HTML page that will redirect to home."
	)
	@APIResponse(
		responseCode = "200",
		description = "User's email was validated.",
		content = @Content(
			mediaType = "text/html"
		)
	)
	@APIResponse(
		responseCode = "400",
		description = "Bad request given. Data given could not pass validation. (No id/ token given, bad token)",
		content = @Content(mediaType = "text/plain")
	)
	@APIResponse(
		responseCode = "404",
		description = "Bad request given. Could not find user from id given.",
		content = @Content(mediaType = "text/plain")
	)
	@Tags({@Tag(name = "User")})
	@Produces(MediaType.TEXT_HTML)
	public Response validateUserEmail(
		@QueryParam("validationToken")
			String validationToken,
		@QueryParam("userId")
			ObjectId userId
	) {
		if(validationToken == null) {
			throw new ValidationException("No token given.");
		}
		if(userId == null) {
			throw new ValidationException("No user id given.");
		}
		
		User user = User.findById(userId);
		if(user == null) {
			throw new EntityNotFoundException("User was not found.");
		}
		
		try {
			this.passwordService.assertPasswordMatchesHash(user.getEmailValidationTokenHash(), validationToken);
		} catch(IncorrectPasswordException e) {
			throw new ValidationException("Token given was invalid.");
		}
		
		user.setEmailValidated(true);
		user.setEmailValidationTokenHash(null);
		user.setLastEmailValidated(new Date());
		
		user.update();
		
		TemplateInstance returnBody = this.redirectResponseTemplate
			.data("responseType", "success")
			.data("message", SUCCESSFULLY_VALIDATED_EMAIL_MESSAGE);
		
		return Response.status(200).type(MediaType.TEXT_HTML).entity(returnBody).build();
	}
	
	//TODO:: endpoint to resend
}
