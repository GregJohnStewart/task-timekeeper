package com.gjs.taskTimekeeper.webServer.server;

import com.gjs.taskTimekeeper.webServer.server.endpoints.ExampleResourceTest;
import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeExampleResourceIT extends ExampleResourceTest {

    // Execute the same tests but in native mode.
}