package com.gjs.taskTimekeeper.webServer.server;

import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import com.gjs.taskTimekeeper.webServer.server.service.DefaultKeysChecker;
import com.gjs.taskTimekeeper.webServer.server.service.ServerUrlService;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.ZonedDateTime;

@ApplicationScoped
@Slf4j
public class LifecycleBean {
	private static final String PACKAGED_KEYS_ERR_MESSAGE = "Using packaged keys. This is unacceptable, see the Admin Guide on the project's github for more information.";
	private static final String PACKAGED_KEYS_ERR_MESSAGE_LINE_2 = "Server will automatically shut down in 10 minutes.";
	private static final String PACKAGED_KEYS_CLOSE_MESSAGE = "Using packaged keys. This is unacceptable, see the Admin Guide on the project's github for more information.";
	
	private ZonedDateTime startDateTime;
	
	private final Template startTemplate;
	private final DefaultKeysChecker defaultKeysChecker;
	private final ServerUrlService serverUrlService;
	
	private final String serverVersion;
	private final String buildTime;
	private final String coreVersion;
	private final String managerIOVersion;
	private final String statsVersion;
	private final String webLibVersion;
	
	public LifecycleBean(
		@ResourcePath("startTemplate")
			Template startTemplate,
		DefaultKeysChecker defaultKeysChecker,
		ServerUrlService serverUrlService,
		@ConfigProperty(name = "version")
			String serverVersion,
		@ConfigProperty(name = "buildtime")
			String buildTime,
		@ConfigProperty(name = "lib.base.core.version")
			String coreVersion,
		@ConfigProperty(name = "lib.base.managerIO.version")
			String managerIOVersion,
		@ConfigProperty(name = "lib.base.stats.version")
			String statsVersion,
		@ConfigProperty(name = "lib.server.webLibrary.version")
			String webLibVersion
	) {
		this.startTemplate = startTemplate;
		this.defaultKeysChecker = defaultKeysChecker;
		this.serverUrlService = serverUrlService;
		
		this.serverVersion = serverVersion;
		this.buildTime = buildTime;
		this.coreVersion = coreVersion;
		this.managerIOVersion = managerIOVersion;
		this.statsVersion = statsVersion;
		this.webLibVersion = webLibVersion;
	}
	
	void onStart(
		@Observes
			StartupEvent ev
	) throws InterruptedException, MalformedURLException {
		this.startDateTime = ZonedDateTime.now();
		log.info("Task Timekeeper Web Server starting.");
		log.info("Base URL: {}", this.serverUrlService.getBaseServerUrl());
		log.debug("Version: {}", this.serverVersion);
		log.debug("build time: {}", this.buildTime);
		log.debug("Core lib version: {}", this.coreVersion);
		log.debug("ManagerIO lib version: {}", this.managerIOVersion);
		log.debug("Stats lib version: {}", this.statsVersion);
		log.debug("Web lib version: {}", this.webLibVersion);
		
		System.out.println(
			this.startTemplate
				.data("baseUrl", this.serverUrlService.getBaseServerUrl())
				.data("serverVersion", this.serverVersion)
				.data("buildTime", this.buildTime)
				.data("coreVersion", this.coreVersion)
				.data("ioVersion", this.managerIOVersion)
				.data("statsVersion", this.statsVersion)
				.data("webVersion", this.webLibVersion)
				.render()
		);
		log.debug("default keys checker: {}", this.defaultKeysChecker);
		if(this.defaultKeysChecker.usingPackagedKeys()) {
			Thread.sleep(100);
			System.err.println(PACKAGED_KEYS_ERR_MESSAGE);
			System.err.println(PACKAGED_KEYS_ERR_MESSAGE_LINE_2);
			
			log.error(PACKAGED_KEYS_ERR_MESSAGE);
			log.error(PACKAGED_KEYS_ERR_MESSAGE_LINE_2);
		}
	}
	
	void onStop(
		@Observes
			ShutdownEvent ev
	) {
		log.info("The server is stopping.");
		Duration runtime = Duration.between(this.startDateTime, ZonedDateTime.now());
		log.info("Server ran for {}", TimeParser.toDurationStringExact(runtime));
	}
	
	/**
	 * Kills the server after 10 minutes if using the packaged keys.
	 * <p>
	 * TODO:: make to only run once/ make it more efficient for the repeat?
	 */
	@Scheduled(every = "1m", delay = 10)
	void bootIfPackagedKeys() {
		if(this.defaultKeysChecker.usingPackagedKeys()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.err.println(PACKAGED_KEYS_CLOSE_MESSAGE);
					log.error(PACKAGED_KEYS_CLOSE_MESSAGE);
					System.exit(1);
				}
			}).start();
		}
	}
}
