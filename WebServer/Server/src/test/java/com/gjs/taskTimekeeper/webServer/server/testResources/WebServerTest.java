package com.gjs.taskTimekeeper.webServer.server.testResources;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class WebServerTest {
}
