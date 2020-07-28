package com.gjs.taskTimekeeper.webServer.server.utils;

import com.gjs.taskTimekeeper.webServer.server.testResources.WebServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaticUtilsTest extends WebServerTest {
	private static final String WORKING_DIR = System.getProperty("user.dir");
	private static final String WORKING_DIR_FILE = "file:" + WORKING_DIR;
	private static final String RESOURCES_DIR = WORKING_DIR + "/build/resources/test/";
	private static final String RESOURCES_DIR_FILE = WORKING_DIR_FILE + "/build/resources/test/";
	
	@Test
	public void testGetResourceAsUrlNull() {
		Assertions.assertThrows(NullPointerException.class, ()->{
			StaticUtils.resourceAsUrl(null);
		});
	}
	
	@Test
	public void testGetResourceAsUrlBad() {
	
	}
	
	@ParameterizedTest
	@MethodSource("resourceAsUrlParameters")
	public void testGetResourceAsUrl(String location, URL expectedUrl) throws MalformedURLException {
		URL gotten = StaticUtils.resourceAsUrl(location);
		
		assertEquals(expectedUrl, gotten);
	}
	
	public static Stream<Arguments> resourceAsUrlParameters() {
		try {
			return Stream.of(
				//fails null
				Arguments.of(
					"application.yaml",
					new URL(RESOURCES_DIR_FILE + "application.yaml")
				),
				Arguments.of(
					RESOURCES_DIR + "application.yaml",
					new URL(RESOURCES_DIR_FILE + "application.yaml")
				)
			);
		} catch(MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
