package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Home {
    private static final Logger LOGGER = LoggerFactory.getLogger(Home.class);

    @Inject
    private ServerInfoBean serverInfoBean;

    @ResourcePath("webPages/pages/home")
    Template homeTemplate;

    @GET
    @Operation(
            summary = "The home page for the UI."
    )
    @Counted(name = "numRequests", description = "How many times the ui was loaded.")
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @APIResponse(
            responseCode = "200",
            description = "Successfully loaded the home page.",
            content = @Content(
                    mediaType = "text/html"
            )
    )
    @Tags({@Tag(name="UI (Web Pages)")})
    public TemplateInstance load() {
        TemplateInstance instance = homeTemplate.data("title", "Home");
        instance = instance.data("serverInfo", this.serverInfoBean);
        instance = instance.data("extraStyle", new String[]{"home.css"});
        instance = instance.data("extraScripts", new String[]{
            "timeManagement/tasks.js",
            "timeManagement/periods.js",
            "timeManagement/selectedPeriod.js",
            "timeManagement/homeTimekeeperFunctions.js",
            "home.js"
        });
    
        return instance;
    }
}
