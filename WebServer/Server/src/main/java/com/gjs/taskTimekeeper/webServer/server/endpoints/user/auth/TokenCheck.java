package com.gjs.taskTimekeeper.webServer.server.endpoints.user.auth;

import com.gjs.taskTimekeeper.webServer.server.utils.StaticUtils;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.TokenCheckResponse;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;

@Path("/api/user/auth/tokenCheck")
@RequestScoped
public class TokenCheck {
	
	@Inject
	JsonWebToken jwt;
	
	@GET()
	@Operation(
		summary = "Checks a users' token."
	)
	@APIResponse(
		responseCode = "200",
		description = "The check happened.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = TokenCheckResponse.class)
		)
	)
	@Tags({@Tag(name = "User"), @Tag(name = "Auth")})
	@SecurityRequirement(name = "JwtAuth")
	@RolesAllowed({"REGULAR", "ADMIN"})
	@Produces(MediaType.APPLICATION_JSON)
	public Response helloRolesAllowed(
		@Context
			SecurityContext ctx
	) {
		TokenCheckResponse response = new TokenCheckResponse();
		if(jwt.getRawToken() != null) {
			response.setHadToken(true);
			response.setTokenSecure(ctx.isSecure());
			response.setExpired(jwt.getExpirationTime() <= StaticUtils.currentTimeInSecs());
			response.setExpirationDate(new Date(jwt.getExpirationTime()));
		}
		return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON_TYPE).entity(response).build();
	}
}
