package com.gjs.taskTimekeeper.webServer.server.endpoints;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.ServerInfo;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/server")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Server {
	
	@Inject
	ServerInfoBean serverInfoBean;
	
	@GET()
	@Counted(name = "numRequests", description = "How many server info requests handled.")
	@Path("/info")
	@Operation(
		summary = "Gets the server's operational/ contact info."
	)
	@APIResponse(
		responseCode = "200",
		description = "The server info.",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ServerInfo.class)
		)
	)
	@Tags({@Tag(name = "Misc")})
	public Response getServerInfo() {
		return Response
			.status(Response.Status.OK)
			.entity(serverInfoBean.toServerInfo())
			.build();
	}
}
