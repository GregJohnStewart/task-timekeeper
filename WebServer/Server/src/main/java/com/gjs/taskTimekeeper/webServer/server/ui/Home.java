package com.gjs.taskTimekeeper.webServer.server.ui;

import com.gjs.taskTimekeeper.webServer.server.config.ServerInfoBean;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import org.eclipse.microprofile.metrics.annotation.Counted;
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
    @Counted(name = "numRequests", description = "How many times he ui was loaded.")
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance load() {
        TemplateInstance instance = homeTemplate.data("title", "Home");
        instance = instance.data("serverInfo", this.serverInfoBean);

        return instance;
    }
}
